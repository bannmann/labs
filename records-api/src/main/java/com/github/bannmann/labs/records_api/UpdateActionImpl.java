package com.github.bannmann.labs.records_api;

import static org.jooq.impl.DSL.inline;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Record2;
import org.jooq.Select;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UpdatableRecord;
import org.jooq.Update;
import org.jooq.UpdateSetFirstStep;
import org.jooq.UpdateSetMoreStep;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.SQLDataType;

import com.github.mizool.core.Identifiable;
import com.github.mizool.core.Identifier;
import com.github.mizool.core.exception.ConflictingEntityException;
import com.github.mizool.core.exception.InvalidPrimaryKeyException;
import com.github.mizool.core.exception.ObjectNotFoundException;
import com.github.mizool.core.exception.ReadonlyFieldException;
import com.github.mizool.core.exception.StoreLayerException;
import com.github.mizool.core.validation.Nullable;

@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("java:S6213")
class UpdateActionImpl<P, R extends UpdatableRecord<R>> implements IUpdateAction<P, R>
{
    private static <R extends UpdatableRecord<R>> void normalizeEmail(R record, TableField<R, String> field)
    {
        adjust(record, field, s -> s.toLowerCase(Locale.ROOT));
    }

    private static <F, R extends UpdatableRecord<R>> void adjust(
        R record, TableField<R, F> field, UnaryOperator<F> adjuster)
    {
        record.set(field, adjuster.apply(record.get(field)));
    }

    private final DSLContext context;

    /**
     * TODO decide if/how to replace with regular 'Clock' without sacrificing Charlie hook for truncating
     */
    private final StoreClock storeClock;

    private final Map<Name, Object> assignments = new HashMap<>();

    private Table<R> table;
    private TableField<R, String> primaryKeyField;
    private Condition primaryKeyCondition;
    private Function<P, R> convertFromPojo;
    private Function<R, P> presetConvertToPojo;
    private R existingRecord;
    private R newRecord;

    /**
     * Non-PK conditions which must hold for the update to pass. <br> Each condition is added as-is to the {@code WHERE}
     * clause. If the update does not affect any rows, the negated version of each condition is used in a {@code SELECT}
     * to distinguish different {@link CheckReason} types.
     */
    private final List<Check> checks = new ArrayList<>();

    @Override
    public <F> void adjusting(@NonNull TableField<R, F> field, @NonNull UnaryOperator<F> adjuster)
    {
        adjust(newRecord, field, adjuster);
    }

    /**
     * Registers the existing pojo for use by immutability checks (built-in) and collision detection (enabled
     * separately).
     */
    @Override
    public void andExistingPojo(@NonNull P existingPojo)
    {
        existingRecord = convertFromPojo.apply(existingPojo);
        if (!newRecord.key()
            .equals(existingRecord.key()))
        {
            throw new InvalidPrimaryKeyException("Primary key mismatch");
        }
    }

    /**
     * Enables collision detection for the given field (based on the pojo's value), then increases it. <br>
     * <br>
     * Invoking this method adds a {@code WHERE} condition similar to {@linkplain #postdetectCollisionIf(Condition,
     * TableField) postdetect} collision check.<br>
     * <br>
     * If an existing pojo has been given, an additional {@linkplain #predetectCollisionOn(TableField) predetect}
     * collision check is performed.<br>
     * <br>
     * Updates with a value mismatch will result in a {@link ConflictingEntityException}. This is the case regardless
     * of whether the collision was detected before or after contacting the database.<br>
     * <br>
     * Note that fields which are used for collision checks do not support {@code null}. Make sure to avoid illegal
     * states by adding {@code NOT NULL} clauses to the respective column DDL.
     *
     * @param field the field to use for collision detection and to increase
     */
    @Override
    public void checkAndIncrease(@NonNull TableField<R, Integer> field)
    {
        performCollisionDetection(field);

        Integer value = newRecord.getValue(field);
        newRecord.set(field, value + 1);
    }

    private void performCollisionDetection(TableField<R, ?> field)
    {
        if (existingRecord != null)
        {
            // comparing update: add predetect AND postdetect check
            predetectCollisionOn(field);
        }
        else
        {
            // basic update: only add postdetect check
            addCollisionCheck(field);
        }
    }

    private <V> void addCollisionCheck(TableField<R, V> field)
    {
        // Represents the old state the client started with; the new one that we are going to write is generated later.
        V valueGivenByClient = newRecord.get(field);

        if (valueGivenByClient == null)
        {
            // TODO we didn't detect a conflict yet. should we add another UnprocessableEntityException subclass?
            throw new ConflictingEntityException(String.format(
                "Field %s of given entity must be non-null to perform collision detection",
                field.getUnqualifiedName()));
        }
        Condition hasCollision = field.notEqual(valueGivenByClient);
        internalAddCheck(new Check(hasCollision, CheckReason.COLLISION_DETECTION, field));
    }

    /**
     * Adds a check for collision detection or field verification. <br>
     * <br>
     * The negated version of the condition is used in the {@code UPDATE ... WHERE} clause together with the primary
     * key.<br>
     * <br>
     * If the update did not change a row, another database round trip is made to determine which, if any, of the checks
     * were the cause for the failed update. In that case, the corresponding {@link CheckReason#getExceptionBuilder()
     * exception builder} is invoked. The exception includes the field/check name. If none of the checks caused the
     * update to fail, the primary key was incorrect and an {@link ObjectNotFoundException} is thrown.
     */
    private boolean internalAddCheck(Check check)
    {
        return checks.add(check);
    }

    /**
     * Enables collision detection for the given field (based on the pojo's value), then randomizes it. <br>
     * <br>
     * Invoking this method adds a {@code WHERE} condition similar to {@linkplain #postdetectCollisionIf(Condition,
     * TableField) postdetect} collision check.<br>
     * <br>
     * If an existing pojo has been given, an additional {@linkplain #predetectCollisionOn(TableField) predetect}
     * collision check is performed.<br>
     * <br>
     * Updates with a value mismatch will result in a {@link ConflictingEntityException}. This is the case regardless of
     * whether the collision was detected before or after contacting the database.<br>
     * <br>
     * Note that fields which are used for collision checks do not support {@code null}. Make sure to avoid illegal
     * states by adding {@code NOT NULL} clauses to the respective column DDL.
     *
     * @param field the field to use for collision detection and to randomize
     * @param randomSupplier the supplier for the new random value to set
     */
    @Override
    public <U> void checkAndRandomize(@NonNull TableField<R, U> field, @NonNull Supplier<U> randomSupplier)
    {
        performCollisionDetection(field);
        newRecord.set(field, randomSupplier.get());
    }

    /**
     * Enables collision detection for the given field (based on the pojo's value), then refreshes it. <br>
     * <br>
     * Invoking this method adds a {@code WHERE} condition similar to {@linkplain #postdetectCollisionIf(Condition,
     * TableField) postdetect} collision check.<br>
     * <br>
     * If an existing pojo has been given, an additional {@linkplain #predetectCollisionOn(TableField) predetect}
     * collision check is performed.<br>
     * <br>
     * Updates with a value mismatch will result in a {@link ConflictingEntityException}. This is the case regardless
     * of whether the collision was detected before or after contacting the database.<br>
     * <br>
     * Note that fields which are used for collision checks do not support {@code null}. Make sure to avoid illegal
     * states by adding {@code NOT NULL} clauses to the respective column DDL.
     *
     * @param field the field to use for collision detection and to refresh
     */
    @Override
    public void checkAndRefresh(@NonNull TableField<R, OffsetDateTime> field)
    {
        performCollisionDetection(field);
        newRecord.set(field, storeClock.now());
    }

    @Override
    public void execute()
    {
        internalExecute();
    }

    private void internalExecute()
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
            Constraints.findFieldOfViolatedForeignKey(e, table).ifPresent(referencingField -> {
                throw new EntityReferenceException(referencingField);
            });

            Constraints.findFieldOfViolatedUniqueOrPrimaryKey(e, table)
                .ifPresent(field -> {
                    throw new ConflictingEntityException("Conflict with existing entity due to " + field);
                });

            // If we get here, violated constraints don't have deterministic names, or it's an unrelated problem.
            throw new StoreLayerException("Error updating " + table.getName(), e);
        }
    }

    private Update<R> createStatement()
    {
        var tableStep = context.update(table);
        var valuesStep = addValuesToSet(tableStep);
        return valuesStep.where(getCombinedCondition());
    }

    private UpdateSetMoreStep<R> addValuesToSet(UpdateSetFirstStep<R> tableStep)
    {
        if (newRecord != null)
        {
            return tableStep.set(newRecord);
        }
        else
        {
            return tableStep.set(assignments);
        }
    }

    private Condition getCombinedCondition()
    {
        Condition result = this.primaryKeyCondition;
        for (Check check : checks)
        {
            result = result.and(check.getSuccessCondition());
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
        Field<String> violationType = inlineVarchar(32,
            check.getReason()
                .toString());
        Field<String> label = inlineVarchar(128,
            check.getLabel()
                .toString());
        return context.select(violationType, label)
            .from(table)
            .where(primaryKeyCondition.and(check.getFailureCondition()));
    }

    private Field<String> inlineVarchar(int length, String value)
    {
        // Without the cast(), there will be trailing spaces which break e.g. Enum.valueOf()
        return inline(value).cast(SQLDataType.VARCHAR(length));
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

    private RecordKey getRecordKey()
    {
        return new RecordKey(String.format("%s{%s}", table.getName(), primaryKeyCondition));
    }

    @Override
    public P executeAndConvert()
    {
        return executeAndConvertVia(presetConvertToPojo);
    }

    @Override
    public P executeAndConvertVia(@NonNull Function<R, P> toPojo)
    {
        internalExecute();
        return toPojo.apply(newRecord);
    }

    @Override
    public void fromNewPojo(@NonNull P newPojo)
    {
        newRecord = convertFromPojo.apply(newPojo);
        initPrimaryKey(newRecord);
    }

    private void initPrimaryKey(@NonNull R newRecord)
    {
        TableField<R, String> field = Tables.obtainPrimaryKey(table);
        String value = field.get(newRecord);
        if (value == null)
        {
            throw new InvalidPrimaryKeyException(String.format("Primary key field %s is null",
                field.getUnqualifiedName()));
        }
        initPrimaryKey(value, field);
    }

    private void initPrimaryKey(@NonNull String value, @NonNull TableField<R, String> primaryKeyField)
    {
        this.primaryKeyField = primaryKeyField;
        primaryKeyCondition = primaryKeyField.eq(value);
    }

    @Override
    public void increase(@NonNull TableField<R, ?> field)
    {
        putAssignment(field, field.plus(1));
    }

    private void putAssignment(TableField<R, ?> field, Object value)
    {
        verifyNotPrimaryKey(field);
        assignments.put(field.getQualifiedName(), value);
    }

    private void verifyNotPrimaryKey(TableField<R, ?> field)
    {
        if (field.equals(primaryKeyField))
        {
            throw new IllegalArgumentException("Cannot alter primary key field");
        }
    }

    @Override
    public void normalizingEmail(@NonNull TableField<R, String> field)
    {
        normalizeEmail(newRecord, field);
    }

    /**
     * Enables collision detection based on the given condition. See {@link #postdetectCollisionIf(Condition,
     * TableField)} for details.
     *
     * @param collisionOccurred the condition which evaluates to true if a collision occurred
     * @param name a string to include in the exception message on failure. For simple one-field conditions, use {@link
     * #postdetectCollisionIf(Condition, TableField)} instead.
     */
    @Override
    public void postdetectCollisionIf(@NonNull Condition collisionOccurred, @NonNull String name)
    {
        internalAddCheck(new Check(collisionOccurred, CheckReason.COLLISION_DETECTION, name));
    }

    /**
     * Enables collision detection based on the given condition. <br>
     * <br>
     * The negated version of the given condition is used in the {@code UPDATE ... WHERE} clause together with the
     * primary key.<br>
     * <br>
     * If the update did not change a row, another database round trip is made to distinguish the 'collision' from the
     * 'invalid primary key' case. If there was a collision, a {@link ConflictingEntityException} will be thrown
     * indicating which field was the cause. Otherwise, an {@link ObjectNotFoundException} is thrown.<br>
     * <br>
     * Note that fields which are used for collision checks do not support {@code null}. Make sure to avoid illegal
     * states by adding {@code NOT NULL} clauses to the respective column DDL.
     *
     * @param collisionOccurred the condition which evaluates to true if a collision occurred
     * @param field the field checked by the condition (included in the exception message on failure). For multi-field
     * conditions, use {@link #postdetectCollisionIf(Condition, String)} instead.
     */
    @Override
    public void postdetectCollisionIf(@NonNull Condition collisionOccurred, @NonNull TableField<R, ?> field)
    {
        internalAddCheck(new Check(collisionOccurred, CheckReason.COLLISION_DETECTION, field));
    }

    /**
     * Enables collision detection by comparing the values of the given field in the given new and existing records
     * before contacting the database. Also enables {@linkplain #postdetectCollisionIf(Condition, TableField) late
     * collision detection (postdetect)}.<br>
     * <br>
     * Note that fields which are used for collision checks do not support {@code null}. Make sure to avoid illegal
     * states by adding {@code NOT NULL} clauses to the respective column DDL.
     */
    @Override
    public void predetectCollisionOn(@NonNull TableField<R, ?> field)
    {
        if (isFieldValueChanged(field))
        {
            throw new ConflictingEntityException(String.format("%s is in a conflicting state (%s)",
                getRecordKey(),
                field.getUnqualifiedName()));
        }

        // As the database row could change in the meantime, we also need to add a postdetect condition
        addCollisionCheck(field);
    }

    private boolean isFieldValueChanged(TableField<R, ?> field)
    {
        return !Objects.equals(newRecord.get(field), existingRecord.get(field));
    }

    @Override
    public void refresh(@NonNull TableField<R, OffsetDateTime> timestampField)
    {
        putAssignment(timestampField, storeClock.now());
    }

    /**
     * @param value may be {@code null}
     */
    @Override
    public <F> void set(@NonNull TableField<R, F> field, @Nullable F value)
    {
        putAssignment(field, value);
    }

    @Override
    public void update(@NonNull Table<R> table)
    {
        this.table = table;
    }

    /**
     * Verifies that the update will not change the value of the given field. <br>
     * <br>
     * Invoking this method adds a {@code WHERE} condition similar to {@linkplain #postdetectCollisionIf(Condition,
     * TableField) postdetect collision checks} to the {@code UPDATE} statement. However, unlike with collision checks,
     * {@code null} is supported and treated as any other value, allowing e.g. store operation A to treat a field as
     * readonly that may or may not have been set yet by store operation B.<br>
     * <br>
     * In addition, if an existing pojo has been given, it is used for an additional comparison before contacting the
     * database.<br>
     * <br>
     * Updates which attempt to change the field value will result in a {@link ReadonlyFieldException} stating that the
     * field is readonly. This is the case regardless of whether the change was detected before or after contacting the
     * database.
     *
     * @param field the field to check
     */
    @Override
    public <V> void verifyUnchanged(@NonNull TableField<R, V> field)
    {
        // comparing update only
        if (isComparingUpdate() && isFieldValueChanged(field))
        {
            throw new ReadonlyFieldException(field.getUnqualifiedName()
                .toString(), getRecordKey().toString());
        }

        // basic & comparing update
        addUnchangedCheck(field);
    }

    private <V> void addUnchangedCheck(TableField<R, V> field)
    {
        V newValue = newRecord.get(field);
        Condition hasChange = createChangeDetectionCondition(field, newValue);
        internalAddCheck(new Check(hasChange, CheckReason.VERIFY_UNCHANGED, field));
    }

    private <V> Condition createChangeDetectionCondition(TableField<R, V> field, V newValue)
    {
        if (newValue == null)
        {
            return field.isNotNull();
        }
        else
        {
            return field.isNull()
                .or(field.notEqual(newValue));
        }
    }

    private boolean isComparingUpdate()
    {
        return existingRecord != null;
    }

    /**
     * @param id the identifier to use as primary key
     * @param identifiableClass required for catching Identifier mixups at compile time
     */
    @Override
    public <I> void withPrimaryKey(@NonNull Identifier<I> id, @NonNull Class<I> identifiableClass)
    {
        initPrimaryKey(id.getValue(), Tables.obtainPrimaryKey(table));
    }

    @Override
    public void withRecordConvertedUsing(@NonNull RecordConverter<P, R> converter)
    {
        withRecordConvertedVia(converter::fromPojo);
        presetConvertToPojo = converter::toPojo;
    }

    /**
     * Supports both {@link Identifiable} and "anonymous" pojos.
     */
    @Override
    public void withRecordConvertedVia(@NonNull Function<P, R> fromPojo)
    {
        convertFromPojo = fromPojo;
    }
}
