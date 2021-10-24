package com.github.bannmann.labs.records_api;

import static org.jooq.impl.DSL.inline;
import static org.jooq.impl.DSL.not;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Select;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UpdatableRecord;
import org.jooq.Update;
import org.jooq.UpdateSetFirstStep;
import org.jooq.UpdateSetMoreStep;
import org.jooq.exception.DataAccessException;

import com.github.mizool.core.Identifiable;
import com.github.mizool.core.Identifier;
import com.github.mizool.core.exception.CodeInconsistencyException;
import com.github.mizool.core.exception.ConflictingEntityException;
import com.github.mizool.core.exception.ObjectNotFoundException;
import com.github.mizool.core.exception.StoreLayerException;
import com.github.mizool.core.exception.UnprocessableEntityException;
import com.github.mizool.core.validation.Nullable;

/**
 * Encapsulates database operations. <br>
 * <br>
 * Unless noted otherwise, all methods throw {@link NullPointerException} if any argument is {@code null}.
 */
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject), access = AccessLevel.PROTECTED)
public class Records implements RecordsApi.EntryPoints
{
    private static <R extends UpdatableRecord<R>> TableField<R, String> obtainPrimaryKeyForIdentifiable(Table<R> table)
    {
        List<TableField<R, ?>> keyFields = table.getPrimaryKey()
            .getFields();
        if (keyFields.size() > 1)
        {
            throw new CodeInconsistencyException(String.format("Table %s has a multi-column primary key",
                table.getName()));
        }

        TableField<R, ?> keyField = keyFields.get(0);
        if (!keyField.getType()
            .equals(String.class))
        {
            throw new CodeInconsistencyException(String.format("Table %s has a non-string primary key",
                table.getName()));
        }

        @SuppressWarnings("unchecked") TableField<R, String> stringKeyField = (TableField<R, String>) keyField;
        return stringKeyField;
    }

    private static <R extends UpdatableRecord<R>> void normalizeEmail(R record, TableField<R, String> field)
    {
        adjust(record, field, s -> s.toLowerCase(Locale.ROOT));
    }

    private static <F, R extends UpdatableRecord<R>> void adjust(
        R record, TableField<R, F> field, UnaryOperator<F> adjuster)
    {
        record.set(field, adjuster.apply(record.get(field)));
    }

    @RequiredArgsConstructor
    private class UpdateInitializer<R extends UpdatableRecord<R>> implements RecordsApi.UpdateStart<R>
    {
        @NonNull
        protected final Table<R> table;

        @Override
        public <P> RecordsApi.UpdateSetPojo<R, P> withRecordConvertedVia(@NonNull Function<P, R> convertFromPojo)
        {
            return new UpdatePojoSetter<>(convertFromPojo);
        }

        @Override
        public <I extends Identifiable<I>> RecordsApi.PartialUpdate<R> withPrimaryKey(
            @NonNull Identifier<I> id, @NonNull Class<I> identifiableClass)
        {
            return new PartialUpdate<>(table, obtainPrimaryKeyForIdentifiable(table), id);
        }
    }

    /**
     * @param <S> see {@link #self()}
     */
    private abstract class AbstractUpdate<R extends UpdatableRecord<R>, S extends AbstractUpdate<R, S>>
    {
        protected final Table<R> table;
        protected final Condition primaryKeyCondition;

        /**
         * Non-PK conditions which must hold for the update to pass. <br>
         * Each condition is added as-is to the {@code WHERE} clause. If the update does not affect any rows, the
         * negated version of each condition is used in a {@code SELECT} to distinguish different
         * {@link CheckReason} types.
         */
        protected final List<Check> checks = new ArrayList<>();

        protected AbstractUpdate(Table<R> table, Condition primaryKeyCondition)
        {
            this.table = table;
            this.primaryKeyCondition = primaryKeyCondition;
        }

        /**
         * For details, see the article "Emulating self types using Java generics"
         * <a href="https://web.archive.org/web/20130721224442/http:/passion.forco.de/content/emulating-self-types-using-java-generics-simplify-fluent-api-implementation">archived here</a>.
         */
        @SuppressWarnings("unchecked")
        protected S self()
        {
            return (S) this;
        }

        public final S postdetectCollisionIf(@NonNull Condition condition, @NonNull String name)
        {
            checks.add(new Check(condition.not(), CheckReason.COLLISION_DETECTION, name));
            return self();
        }

        public final S postdetectCollisionIf(@NonNull Condition condition, @NonNull TableField field)
        {
            checks.add(new Check(condition.not(), CheckReason.COLLISION_DETECTION, field));
            return self();
        }

        protected void internalExecute()
        {
            try
            {
                Update<R> update = createStatement();

                int updatedRows = update.execute();
                if (updatedRows != 1)
                {
                    throw createException();
                }
            }
            catch (DataAccessException e)
            {
                throw new StoreLayerException("Error updating " + table.getName(), e);
            }
        }

        private Update<R> createStatement()
        {
            UpdateSetFirstStep<R> tableStep = context.update(table);
            UpdateSetMoreStep<R> valuesStep = addValuesToSet(tableStep);
            return valuesStep.where(getCombinedCondition());
        }

        protected abstract UpdateSetMoreStep<R> addValuesToSet(UpdateSetFirstStep<R> tableStep);

        private Condition getCombinedCondition()
        {
            Condition result = this.primaryKeyCondition;
            for (Check check : checks)
            {
                result = result.and(check.getCondition());
            }
            return result;
        }

        private RuntimeException createException()
        {
            Function<RecordKey, RuntimeException> exceptionBuilder = checks.stream()
                .map(this::toViolationDetectionSelect)
                .reduce(Select::unionAll)
                .stream()
                .map(this::fetchAndConvertToViolations)
                .peek(violations -> log.debug("Detected violations: {}", violations))
                .flatMap(List::stream)
                .map(Violation::getExceptionBuilder)
                .findFirst()
                .orElse(recordKey -> new ObjectNotFoundException(recordKey + " not found"));

            return exceptionBuilder.apply(getRecordKey());
        }

        private Select<Record2<String, String>> toViolationDetectionSelect(Check check)
        {
            Field<String> violationType = inline(check.getReason()
                .toString()).as("violation_type");
            Field<String> label = inline(check.getLabel()).as("label");
            return context.select(violationType, label)
                .from(table)
                .where(primaryKeyCondition.and(not(check.getCondition())));
        }

        private List<Violation> fetchAndConvertToViolations(Select<Record2<String, String>> record)
        {
            return record.fetch(this::recordToViolation);
        }

        private Violation recordToViolation(Record2<String, String> record)
        {
            return Violation.builder()
                .checkReason(CheckReason.valueOf(record.value1()))
                .checkLabel(new CheckLabel(record.value2()))
                .build();
        }

        protected RecordKey getRecordKey()
        {
            return new RecordKey(String.format("%s{%s}", table.getName(), primaryKeyCondition));
        }
    }

    @Value
    private static class Check
    {
        @NonNull Condition condition;

        @NonNull CheckReason reason;

        Field<?> field;

        String name;

        public Check(@NonNull Condition condition, @NonNull CheckReason reason, @NonNull Field<?> field)
        {
            this.condition = condition;
            this.reason = reason;
            this.field = field;
            this.name = null;
        }

        public Check(@NonNull Condition condition, @NonNull CheckReason reason, @NonNull String name)
        {
            this.condition = condition;
            this.reason = reason;
            this.name = name;
            this.field = null;
        }

        /**
         * @return the field (if given) or the user-provided name of this check
         */
        public CheckLabel getLabel()
        {
            String result = name;
            if (field != null)
            {
                result = field.getQualifiedName()
                    .last();
            }
            return new CheckLabel(result);
        }
    }

    @RequiredArgsConstructor
    @Getter
    private enum CheckReason
    {
        COLLISION_DETECTION((recordKey, checkLabel) -> {
            return new ConflictingEntityException(String.format("%s is in a conflicting state (%s)",
                recordKey,
                checkLabel));
        }),
        VERIFY_UNCHANGED((recordKey, checkLabel) -> {
            return new UnprocessableEntityException(String.format("Attempt to update readonly field '%s' of %s",
                checkLabel,
                recordKey));
        });

        @NonNull
        private final BiFunction<RecordKey, CheckLabel, RuntimeException> exceptionBuilder;
    }

    /**
     * Wraps a string suitable for log/exception messages containing table name and primary key of a record.
     */
    private static final class RecordKey extends StringWrapper
    {
        public RecordKey(String contents)
        {
            super(contents);
        }
    }

    /**
     * Wraps the field name (if given) or the user-provided name of a check.
     */
    private static final class CheckLabel extends StringWrapper
    {
        public CheckLabel(String contents)
        {
            super(contents);
        }
    }

    @Value
    @Builder
    private static class Violation
    {
        CheckReason checkReason;

        /**
         * The field name (if given) or the user-provided name of the violated check
         */
        CheckLabel checkLabel;

        Function<RecordKey, RuntimeException> getExceptionBuilder()
        {
            return recordKey -> checkReason.getExceptionBuilder()
                .apply(recordKey, checkLabel);
        }
    }

    private final class PartialUpdate<R extends UpdatableRecord<R>> extends AbstractUpdate<R, PartialUpdate<R>>
        implements RecordsApi.PartialUpdatesRegistered<R>
    {
        private final Map<Name, Object> assignments = new HashMap<>();
        private final TableField<R, String> primaryKeyField;

        public PartialUpdate(
            @NonNull Table<R> table, @NonNull TableField<R, String> primaryKeyField, @NonNull Identifier<?> id)
        {
            super(table, primaryKeyField.eq(id.getValue()));
            this.primaryKeyField = primaryKeyField;
        }

        @Override
        public <F> RecordsApi.PartialUpdatesRegistered<R> set(@NonNull TableField<R, F> field, @Nullable F value)
        {
            return putAssignment(field, value);
        }

        private PartialUpdate<R> putAssignment(TableField<R, ?> field, Object value)
        {
            verifyNotPrimaryKey(field);
            assignments.put(field.getQualifiedName(), value);
            return this;
        }

        private <F> void verifyNotPrimaryKey(TableField<R, F> field)
        {
            if (field.equals(primaryKeyField))
            {
                throw new IllegalArgumentException("Cannot alter primary key field");
            }
        }

        @Override
        public <F> RecordsApi.PartialUpdatesRegistered<R> increase(@NonNull TableField<R, F> field)
        {
            return putAssignment(field, field.plus(1));
        }

        @Override
        public RecordsApi.PartialUpdatesRegistered<R> refresh(@NonNull TableField<R, OffsetDateTime> field)
        {
            return putAssignment(field, storeClock.now());
        }

        @Override
        protected UpdateSetMoreStep<R> addValuesToSet(UpdateSetFirstStep<R> tableStep)
        {
            return tableStep.set(assignments);
        }

        @Override
        public void execute()
        {
            internalExecute();
        }
    }

    @RequiredArgsConstructor
    private class UpdatePojoSetter<R extends UpdatableRecord<R>, P> implements RecordsApi.UpdateSetPojo<R, P>
    {
        @NonNull
        private final Function<P, R> convertFromPojo;

        @Override
        public RecordsApi.FullUpdateStart<R, P> fromNewPojo(@NonNull P newPojo)
        {
            return new BasicUpdate<>(newPojo, convertFromPojo);
        }
    }

    private static Condition buildPrimaryKeyCondition(UpdatableRecord<?> record)
    {
        return Arrays.stream(record.key()
            .fields())
            .map(field -> toCondition(field, record))
            .reduce(Condition::and)
            .orElseThrow(() -> new CodeInconsistencyException("No primary key fields"));
    }

    private static <V> Condition toCondition(Field<V> field, Record record)
    {
        V fieldValue = field.getValue(record);
        if (fieldValue == null)
        {
            throw new UnprocessableEntityException("Primary key field " + field + " is null");
        }
        return field.eq(fieldValue);
    }

    private abstract class AbstractBasicUpdate<R extends UpdatableRecord<R>, P, S extends AbstractBasicUpdate<R, P, S>>
        extends AbstractUpdate<R, S>
    {
        protected final Function<P, R> convertFromPojo;
        protected final R newRecord;

        public AbstractBasicUpdate(
            @NonNull Table<R> table,
            @NonNull Condition primaryKeyCondition,
            @NonNull Function<P, R> convertFromPojo,
            @NonNull R newRecord)
        {
            super(table, primaryKeyCondition);
            this.convertFromPojo = convertFromPojo;
            this.newRecord = newRecord;
        }

        @Override
        protected final UpdateSetMoreStep<R> addValuesToSet(UpdateSetFirstStep<R> tableStep)
        {
            return tableStep.set(newRecord);
        }

        public final <F> S adjusting(@NonNull TableField<R, F> field, @NonNull UnaryOperator<F> adjuster)
        {
            adjust(newRecord, field, adjuster);
            return self();
        }

        public final S normalizingEmail(@NonNull TableField<R, String> field)
        {
            normalizeEmail(newRecord, field);
            return self();
        }

        public final S checkAndRefresh(@NonNull TableField<R, OffsetDateTime> field)
        {
            performCollisionDetection(field);
            newRecord.set(field, storeClock.now());
            return self();
        }

        protected void performCollisionDetection(TableField<R, ?> field)
        {
            // TODO should we implement the behavior "allow PUT if timestamp=null", as well? [1 of 2]
            addCheckForValueOfNewRecord(field, CheckReason.COLLISION_DETECTION);
        }

        /**
         * Used for both collision detection and field verification.
         */
        protected final <V> void addCheckForValueOfNewRecord(TableField<R, V> field, CheckReason reason)
        {
            V newValue = newRecord.get(field);
            Condition condition = field.eq(newValue);
            checks.add(new Check(condition, reason, field));
        }

        public final S checkAndIncrease(@NonNull TableField<R, Integer> field)
        {
            performCollisionDetection(field);

            Integer value = newRecord.getValue(field);
            newRecord.set(field, value + 1);

            return self();
        }

        public final <U> S checkAndRandomize(@NonNull TableField<R, U> field, @NonNull Supplier<U> randomSupplier)
        {
            performCollisionDetection(field);
            newRecord.set(field, randomSupplier.get());
            return self();
        }

        public final P executeAndConvert(@NonNull Function<R, P> convertToPojo)
        {
            internalExecute();
            return convertToPojo.apply(newRecord);
        }
    }

    private class BasicUpdate<R extends UpdatableRecord<R>, P> extends AbstractBasicUpdate<R, P, BasicUpdate<R, P>>
        implements RecordsApi.FullUpdateStart<R, P>
    {
        public BasicUpdate(@NonNull P newPojo, @NonNull Function<P, R> convertFromPojo)
        {
            this(convertFromPojo, convertFromPojo.apply(newPojo));
        }

        private BasicUpdate(Function<P, R> convertFromPojo, R newRecord)
        {
            this(convertFromPojo, newRecord.getTable(), buildPrimaryKeyCondition(newRecord), newRecord);
        }

        protected BasicUpdate(
            Function<P, R> convertFromPojo, Table<R> table, Condition primaryKeyCondition, R newRecord)
        {
            super(table, primaryKeyCondition, convertFromPojo, newRecord);
        }

        @Override
        public RecordsApi.FullUpdate<R, P> verifyUnchanged(@NonNull TableField<R, ?> field)
        {
            addCheckForValueOfNewRecord(field, CheckReason.VERIFY_UNCHANGED);
            return this;
        }

        @Override
        public RecordsApi.FullUpdateWithExisting<R, P> andExistingPojo(@NonNull P existingPojo)
        {
            R existingRecord = convertFromPojo.apply(existingPojo);
            if (!newRecord.key()
                .equals(existingRecord.key()))
            {
                throw new UnprocessableEntityException("Primary key mismatch");
            }

            return new ComparingUpdate<>(table, primaryKeyCondition, convertFromPojo, newRecord, existingRecord);
        }
    }

    private class ComparingUpdate<R extends UpdatableRecord<R>, P>
        extends AbstractBasicUpdate<R, P, ComparingUpdate<R, P>> implements RecordsApi.FullUpdateWithExisting<R, P>
    {
        private final R existingRecord;

        public ComparingUpdate(
            @NonNull Table<R> table,
            @NonNull Condition primaryKeyCondition,
            @NonNull Function<P, R> convertFromPojo,
            @NonNull R newRecord,
            @NonNull R existingRecord)
        {
            super(table, primaryKeyCondition, convertFromPojo, newRecord);
            this.existingRecord = existingRecord;
        }

        @Override
        protected void performCollisionDetection(TableField<R, ?> field)
        {
            predetectCollisionOn(field);
        }

        @Override
        public <V> ComparingUpdate<R, P> predetectCollisionOn(@NonNull TableField<R, V> field)
        {
            // TODO should we implement the behavior "allow PUT if timestamp=null", as well? [2 of 3]
            if (isFieldValueChanged(field))
            {
                throw new ConflictingEntityException(String.format("%s is in a conflicting state (%s)",
                    getRecordKey(),
                    field.getUnqualifiedName()));
            }

            // As the database row could change in the meantime, we also need to add a postdetect condition
            // TODO should we implement the behavior "allow PUT if timestamp=null", as well? [2 of 3]
            addCheckForValueOfNewRecord(field, CheckReason.COLLISION_DETECTION);

            return this;
        }

        private <V> boolean isFieldValueChanged(TableField<R, V> field)
        {
            return !Objects.equals(newRecord.get(field), existingRecord.get(field));
        }

        @Override
        public RecordsApi.FullUpdateWithExisting<R, P> verifyUnchanged(@NonNull TableField<R, ?> field)
        {
            if (isFieldValueChanged(field))
            {
                throw new UnprocessableEntityException(String.format("Attempt to update readonly field '%s' of %s",
                    field.getUnqualifiedName(),
                    getRecordKey()));
            }

            // As the database row could change in the meantime, we also need to add a postdetect condition
            addCheckForValueOfNewRecord(field, CheckReason.VERIFY_UNCHANGED);

            return this;
        }
    }

    private final DSLContext context;

    /**
     * TODO decide if/how to replace with regular 'Clock' without sacrificing Charlie hook for truncating
     */
    private final StoreClock storeClock;

    @Override
    public <R extends UpdatableRecord<R>> RecordsApi.InsertStart<R> insertInto(@NonNull Table<R> table)
    {
        return new InsertInitializer<>();
    }

    @Override
    public <R extends UpdatableRecord<R>> RecordsApi.UpdateStart<R> update(@NonNull Table<R> table)
    {
        return new UpdateInitializer<>(table);
    }

    private class InsertInitializer<R extends UpdatableRecord<R>> implements RecordsApi.InsertStart<R>
    {
        @Override
        public <P extends Identifiable<P>> RecordsApi.InsertSetPojo<R, P> withIdentifiableConvertedVia(@NonNull Function<P, R> convertFromPojo)
        {
            return new InsertPojoSetter<>(pojo -> checkAndConvertIdentifiable(pojo, convertFromPojo));
        }

        private <P extends Identifiable<P>> R checkAndConvertIdentifiable(P pojo, Function<P, R> convertFromPojo)
        {
            if (pojo.getIdentifier() != null)
            {
                throw new UnprocessableEntityException("Identifier must not be specified for new entities");
            }

            R record = convertFromPojo.apply(pojo);

            TableField<R, String> primaryKeyField = obtainPrimaryKeyForIdentifiable(record.getTable());
            record.set(primaryKeyField,
                Identifier.forPojo(pojo.getClass())
                    .random()
                    .getValue());

            return record;
        }

        @Override
        public <P> RecordsApi.InsertSetPojo<R, P> withAnonymousConvertedVia(@NonNull Function<P, R> fromPojo)
        {
            Function<P, R> conversion = fromPojo.compose(this::checkNonIdentifiable);
            return new InsertPojoSetter<>(conversion);
        }

        private <P> P checkNonIdentifiable(P pojo)
        {
            if (pojo instanceof Identifiable<?>)
            {
                throw new IllegalArgumentException("Cannot treat Identifiable entity as anonymous");
            }
            return pojo;
        }
    }

    @RequiredArgsConstructor
    private class InsertPojoSetter<R extends UpdatableRecord<R>, P> implements RecordsApi.InsertSetPojo<R, P>
    {
        @NonNull
        private final Function<P, R> convertFromPojo;

        @Override
        public RecordsApi.InsertMain<R, P> fromPojo(@NonNull P pojo)
        {
            R record = convertFromPojo.apply(pojo);

            return new MainInsert<>(record);
        }
    }

    @RequiredArgsConstructor
    private class MainInsert<R extends UpdatableRecord<R>, P> implements RecordsApi.InsertMain<R, P>
    {
        @NonNull
        private final R record;

        @Override
        public RecordsApi.InsertMain<R, P> generating(@NonNull TableField<R, OffsetDateTime> field)
        {
            if (record.get(field) != null)
            {
                throw new UnprocessableEntityException(String.format(
                    "Generated field %s must not be specified for new entities",
                    field.getName()));
            }

            record.set(field, storeClock.now());

            return this;
        }

        @Override
        public RecordsApi.InsertMain<R, P> normalizingEmail(@NonNull TableField<R, String> field)
        {
            normalizeEmail(record, field);
            return this;
        }

        @Override
        public <F> RecordsApi.InsertMain<R, P> adjusting(
            @NonNull TableField<R, F> field, @NonNull UnaryOperator<F> adjuster)
        {
            adjust(record, field, adjuster);
            return this;
        }

        @Override
        public P executeAndConvert(@NonNull Function<R, P> convertToPojo)
        {
            try
            {
                int insertedRows = context.executeInsert(record);
                if (insertedRows != 1)
                {
                    throw new StoreLayerException("No row was inserted into " +
                        record.getTable()
                            .getName());
                }

                return convertToPojo.apply(record);
            }
            catch (DataAccessException e)
            {
                throw new StoreLayerException("Error inserting into " +
                    record.getTable()
                        .getName(), e);
            }
        }
    }
}
