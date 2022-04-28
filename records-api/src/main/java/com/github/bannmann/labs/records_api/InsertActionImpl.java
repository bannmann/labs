package com.github.bannmann.labs.records_api;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UpdatableRecord;
import org.jooq.exception.DataAccessException;

import com.github.mizool.core.Identifiable;
import com.github.mizool.core.Identifier;
import com.github.mizool.core.exception.ConflictingEntityException;
import com.github.mizool.core.exception.GeneratedFieldOverrideException;
import com.github.mizool.core.exception.StoreLayerException;

@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("java:S6213")
class InsertActionImpl<P, R extends UpdatableRecord<R>> implements IInsertAction<P, R>
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

    private Function<P, R> convertFromPojo;
    private Function<R, P> presetConvertToPojo;
    private R record;

    @Override
    public <F> void adjusting(@NonNull TableField<R, F> field, @NonNull UnaryOperator<F> adjuster)
    {
        adjust(record, field, adjuster);
    }

    @Override
    public P executeAndConvert()
    {
        return executeAndConvertVia(presetConvertToPojo);
    }

    @Override
    public P executeAndConvertVia(@NonNull Function<R, P> toPojo)
    {
        try
        {
            context.executeInsert(record);
            return toPojo.apply(record);
        }
        catch (DataAccessException e)
        {
            Table<R> table = record.getTable();

            Constraints.findFieldOfViolatedForeignKey(e, table)
                .ifPresent(referencingField -> {
                    throw new EntityReferenceException(referencingField);
                });

            Constraints.findFieldOfViolatedUniqueOrPrimaryKey(e, table)
                .ifPresent(field -> {
                    throw new ConflictingEntityException("Conflict with existing entity due to " + field);
                });

            // If we get here, violated constraints don't have deterministic names, or it's an unrelated problem.
            throw new StoreLayerException("Error inserting into " + table.getName(), e);
        }
    }

    @Override
    public void fromPojo(@NonNull P pojo)
    {
        record = convertFromPojo.apply(pojo);
    }

    @Override
    public void generating(@NonNull TableField<R, OffsetDateTime> field)
    {
        if (record.get(field) != null)
        {
            throw new GeneratedFieldOverrideException(field.getName());
        }

        record.set(field, storeClock.now());
    }

    @Override
    public void insertInto(@NonNull Table<R> table)
    {
        // No-op - we don't use the table at all, it's just there for type checking arguments further down the chain
    }

    @Override
    public void normalizingEmail(@NonNull TableField<R, String> field)
    {
        normalizeEmail(record, field);
    }

    @Override
    public void withAnonymousConvertedUsing(@NonNull RecordConverter<P, R> converter)
    {
        withAnonymousConvertedVia(converter::fromPojo);
        presetConvertToPojo = converter::toPojo;
    }

    @Override
    public void withAnonymousConvertedVia(@NonNull Function<P, R> fromPojo)
    {
        convertFromPojo = fromPojo.compose(this::checkNonIdentifiable);
    }

    private P checkNonIdentifiable(P pojo)
    {
        if (pojo instanceof Identifiable<?>)
        {
            throw new IllegalArgumentException("Cannot treat Identifiable entity as anonymous");
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
        Identifiable<?> identifiablePojo = (Identifiable<?>) pojo;
        if (identifiablePojo.getId() != null)
        {
            throw new GeneratedFieldOverrideException("id");
        }

        R resultRecord = convertFromPojo.apply(pojo);

        TableField<R, String> primaryKeyField = Tables.obtainPrimaryKey(resultRecord.getTable());
        resultRecord.set(primaryKeyField,
            Identifier.forPojo(pojo.getClass())
                .random()
                .getValue());

        return resultRecord;
    }
}
