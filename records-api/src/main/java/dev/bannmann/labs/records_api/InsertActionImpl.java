package dev.bannmann.labs.records_api;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UpdatableRecord;
import org.jooq.exception.DataAccessException;
import org.jspecify.annotations.NullMarked;

import com.github.mizool.core.Identifiable;
import com.github.mizool.core.Identifier;
import com.github.mizool.core.concurrent.Lazy;
import com.github.mizool.core.exception.ConflictingEntityException;
import com.github.mizool.core.exception.GeneratedFieldOverrideException;
import com.github.mizool.core.exception.InvalidPrimaryKeyException;
import com.github.mizool.core.exception.StoreLayerException;
import dev.bannmann.labs.annotations.SuppressWarningsRationale;

@Slf4j
@SuppressWarnings("java:S6213")
@SuppressWarningsRationale("The parameter name 'record' is perfectly fine here.")
@NullMarked
class InsertActionImpl<P, R extends UpdatableRecord<R>> implements IInsertAction<P, R>
{
    private static class RecordHolder<R extends UpdatableRecord<R>>
    {
        private final Set<TableField<R, ?>> excludedFields = new HashSet<>();

        private final List<R> records;

        private boolean ignoreDuplicateKey;

        public RecordHolder(@NonNull R record)
        {
            records = List.of(record);
        }

        public RecordHolder(@NonNull List<R> records)
        {
            if (records.isEmpty())
            {
                throw new IllegalArgumentException("Records list cannot be empty");
            }
            this.records = new ArrayList<>(records);
        }

        public void apply(Consumer<R> consumer)
        {
            for (R record : records)
            {
                consumer.accept(record);
            }
        }

        public R executeSingle(DSLContext context)
        {
            verifySingleRecordMode();

            R record = records.getFirst();

            Map<String, Object> values = getInsertableValues(record);

            if (ignoreDuplicateKey)
            {
                record = context.insertInto(record.getTable())
                    .set(values)
                    .onDuplicateKeyIgnore()
                    .returning(record.getTable()
                        .fields())
                    .fetchOne();
            }
            else
            {
                record = context.insertInto(record.getTable())
                    .set(values)
                    .returning(record.getTable()
                        .fields())
                    .fetchOne();
            }
            return record;
        }

        private Map<String, Object> getInsertableValues(R record)
        {
            Map<String, Object> values = record.intoMap();
            for (TableField<R, ?> excludedField : excludedFields)
            {
                values.remove(excludedField.getName());
            }
            return values;
        }

        private void verifySingleRecordMode()
        {
            if (isMultiple())
            {
                throw new IllegalStateException("Multiple records");
            }
        }

        private boolean isMultiple()
        {
            return records.size() > 1;
        }

        public void executeSingleOrBatch(DSLContext context)
        {
            if (isMultiple())
            {
                executeBatch(context);
            }
            else
            {
                executeSingle(context);
            }
        }

        public void executeBatch(DSLContext context)
        {
            context.batchInsert(records)
                .execute();
        }

        public Table<R> getTable()
        {
            return records.getFirst()
                .getTable();
        }

        public void onDuplicateKeyIgnore()
        {
            verifySingleRecordMode();
            ignoreDuplicateKey = true;
        }

        public void excludeField(TableField<R, ?> field)
        {
            excludedFields.add(field);
        }
    }

    private final DSLContext context;
    private final Lazy<OffsetDateTime> now;

    private Function<P, R> convertFromPojo;
    private Function<R, P> presetConvertToPojo;
    private RecordHolder<R> recordHolder;
    private boolean usePresetId;

    public InsertActionImpl(DSLContext context, StoreClock storeClock)
    {
        this.context = context;

        // Encapsulate "now()" calls in a Lazy to ensure multiple timestamp fields all get the same value
        now = new Lazy<>(storeClock::now);
    }

    @Override
    public <F> void adjusting(@NonNull TableField<R, F> field, @NonNull UnaryOperator<F> adjuster)
    {
        recordHolder.apply(record -> record.set(field, adjuster.apply(record.get(field))));
    }

    @Override
    public P executeAndConvert()
    {
        return executeAndConvertVia(presetConvertToPojo);
    }

    @Override
    public P executeAndConvertVia(@NonNull Function<R, P> toPojo)
    {
        R resultRecord = executeSingle();
        return toPojo.apply(resultRecord);
    }

    private R executeSingle()
    {
        try
        {
            return recordHolder.executeSingle(context);
        }
        catch (DataAccessException e)
        {
            throw convertException(e);
        }
    }

    private RuntimeException convertException(DataAccessException e)
    {
        Table<R> table = recordHolder.getTable();

        Optional<String> fieldOfViolatedForeignKey = Constraints.findFieldOfViolatedForeignKey(e, table);
        if (fieldOfViolatedForeignKey.isPresent())
        {
            return new EntityReferenceException(fieldOfViolatedForeignKey.get(), e);
        }

        Optional<String> fieldOfViolatedUniqueOrPrimaryKey = Constraints.findFieldOfViolatedUniqueOrPrimaryKey(e,
            table);
        if (fieldOfViolatedUniqueOrPrimaryKey.isPresent())
        {
            return new ConflictingEntityException("Conflict with existing entity due to " +
                fieldOfViolatedUniqueOrPrimaryKey.get(), e);
        }

        // If we get here, violated constraints don't have deterministic names, or it's an unrelated problem.
        return new StoreLayerException("Error inserting into " + table.getName(), e);
    }

    @Override
    public void fromPojo(@NonNull P pojo)
    {
        R record = convertFromPojo.apply(pojo);
        recordHolder = new RecordHolder<>(record);
    }

    @Override
    public void fromPojos(List<P> pojos)
    {
        List<R> records = pojos.stream()
            .map(pojo -> convertFromPojo.apply(pojo))
            .collect(Collectors.toList());
        recordHolder = new RecordHolder<>(records);
    }

    @Override
    public void fromPojoWithPresetId(P pojo)
    {
        usePresetId = true;
        fromPojo(pojo);
    }

    @Override
    public void fromPojosWithPresetId(List<P> pojos)
    {
        usePresetId = true;
        fromPojos(pojos);
    }

    /**
     * Verifies that the pojo does not specify a value for the given field, then sets the field to 'now'.
     *
     * @throws GeneratedFieldOverrideException if the pojo has a non-{@code null} value for the given field
     */
    @Override
    public void generating(@NonNull TableField<R, OffsetDateTime> field)
    {
        recordHolder.apply(record -> {
            verifyFieldIsNull(field, record, GeneratedFieldOverrideException::new);
            record.set(field, now.get());
        });
    }

    private void verifyFieldIsNull(
        TableField<R, ?> field, R record, Function<String, ? extends RuntimeException> exceptionConstructor)
    {
        if (record.get(field) != null)
        {
            throw exceptionConstructor.apply(field.getName());
        }
    }

    @Override
    public void insertInto(@NonNull Table<R> table)
    {
        // No-op - we don't use the table at all, it's just there for type checking arguments further down the chain
    }

    /**
     * Verifies that the pojo does not specify a value for the given field so that the column default is applied. This
     * is important to ensure {@code SERIAL} columns are never written with user-supplied values.
     *
     * @throws GeneratedFieldOverrideException if the pojo has a non-{@code null} value for the given field
     */
    @Override
    public void keepGeneratedDefault(TableField<R, ?> field)
    {
        recordHolder.excludeField(field);
        recordHolder.apply(record -> {
            verifyFieldIsNull(field, record, GeneratedFieldOverrideException::new);

            // Tell jOOQ to not write the null value, as that would violate the 'not null' constraint
            record.reset(field);
        });
    }

    @Override
    public void normalizingEmail(@NonNull TableField<R, String> field)
    {
        adjusting(field, s -> s.toLowerCase(Locale.ROOT));
    }

    @Override
    public void onDuplicateKeyIgnore()
    {
        recordHolder.onDuplicateKeyIgnore();
    }

    /**
     * Verifies that the pojo does not specify a value for the given field.
     *
     * @throws FieldExclusionException if the pojo has a non-{@code null} value for the given field
     */
    @Override
    public void requireNull(TableField<R, ?> field)
    {
        recordHolder.apply(record -> verifyFieldIsNull(field, record, FieldExclusionException::new));
    }

    @Override
    public void voidExecute()
    {
        try
        {
            recordHolder.executeSingleOrBatch(context);
        }
        catch (DataAccessException e)
        {
            throw convertException(e);
        }
    }

    @Override
    public void withCustomKeyedConvertedUsing(@NonNull RecordConverter<P, R> converter)
    {
        withCustomKeyedConvertedVia(converter::fromPojo);
        presetConvertToPojo = converter::toPojo;
    }

    @Override
    public void withCustomKeyedConvertedVia(@NonNull Function<P, R> fromPojo)
    {
        convertFromPojo = fromPojo.compose(this::checkNonIdentifiable);
    }

    private P checkNonIdentifiable(P pojo)
    {
        if (pojo instanceof Identifiable<?>)
        {
            throw new IllegalArgumentException("Cannot treat Identifiable entity as custom keyed");
        }
        return pojo;
    }

    @Override
    public void withIdentifiableConvertedUsing(@NonNull RecordConverter<P, R> converter)
    {
        withIdentifiableConvertedVia(converter::fromPojo);
        presetConvertToPojo = converter::toPojo;
    }

    @Override
    public void withIdentifiableConvertedVia(@NonNull Function<P, R> fromPojo)
    {
        convertFromPojo = pojo -> checkAndConvertIdentifiable(pojo, fromPojo);
    }

    private R checkAndConvertIdentifiable(P pojo, Function<P, R> convertFromPojo)
    {
        verifyIdSetOrUnsetAsAppropriate(pojo);

        R resultRecord = convertFromPojo.apply(pojo);

        if (!usePresetId)
        {
            TableField<R, String> primaryKeyField = Tables.obtainSingleStringPrimaryKeyField(resultRecord.getTable());
            resultRecord.set(primaryKeyField,
                Identifier.forPojo(pojo.getClass())
                    .random()
                    .getValue());
        }

        return resultRecord;
    }

    private void verifyIdSetOrUnsetAsAppropriate(P pojo)
    {
        Identifiable<?> identifiablePojo = (Identifiable<?>) pojo;
        Identifier<?> pojoId = identifiablePojo.getId();
        if (pojoId != null && !usePresetId)
        {
            throw new GeneratedFieldOverrideException("id");
        }
        if (usePresetId && pojoId == null)
        {
            throw new InvalidPrimaryKeyException("ID of pojo passed to fromPojoWithPresetId() was null");
        }
    }
}
