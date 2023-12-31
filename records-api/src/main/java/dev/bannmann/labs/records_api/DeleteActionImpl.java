package dev.bannmann.labs.records_api;

import java.util.Optional;
import java.util.function.Supplier;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UpdatableRecord;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import com.github.mizool.core.Identifier;
import com.github.mizool.core.exception.ObjectNotFoundException;
import com.github.mizool.core.exception.PermissionDeniedException;
import com.github.mizool.core.exception.StoreLayerException;

@Slf4j
@SuppressWarnings("java:S6213")
class DeleteActionImpl<P, R extends UpdatableRecord<R>> implements IDeleteAction<P, R>
{
    private final DSLContext context;

    private Table<R> table;
    private RecordConverter<P, R> converter;
    private Condition primaryKeyCondition;

    /**
     * Non-PK conditions which must hold for the deletion to happen. <br>
     * <br>
     * The condition is added as-is to the {@code WHERE} clause. If no rows are deleted, the negated version of this
     * condition is used in a {@code SELECT} to distinguish "object not found" from "permission denied".
     */
    private Condition deletionPermittedCondition;

    public DeleteActionImpl(DSLContext context)
    {
        this.context = context;
    }

    @Override
    public void delete()
    {
        // Nothing to do here.
    }

    @Override
    public void denyUnless(@NonNull Condition condition)
    {
        deletionPermittedCondition = condition;
    }

    @Override
    public P executeAndConvert()
    {
        return tryExecuteAndConvert().orElseThrow(ObjectNotFoundException::new);
    }

    @Override
    public void fromIdentifiable(
        @NonNull Table<R> table, @NonNull Class<? extends RecordConverter<P, R>> converterClass)
    {
        this.table = table;

        // Note: the converterClass argument only exists for type safety further down the chain
    }

    @Override
    public void fromIdentifiable(@NonNull Table<R> table, @NonNull RecordConverter<P, R> converter)
    {
        this.table = table;
        this.converter = converter;
    }

    private Condition getCombinedCondition()
    {
        Condition result = this.primaryKeyCondition;
        if (deletionPermittedCondition != null)
        {
            result = result.and(deletionPermittedCondition);
        }
        return result;
    }

    @Override
    public void ignoreIfNotFound()
    {
        // Nothing to do. This method only exists to unlock the tryExecute*() methods in the chain.
    }

    private <T> T internalExecute(Supplier<T> deleteCall)
    {
        try
        {
            return deleteCall.get();
        }
        catch (DataAccessException e)
        {
            throw new StoreLayerException("Error deleting from " + table.getName(), e);
        }
    }

    private void throwPermissionDeniedIfPrimaryKeyExists()
    {
        if (context.fetchExists(DSL.selectFrom(table)
            .where(primaryKeyCondition)))
        {
            throw new PermissionDeniedException();
        }
    }

    @Override
    public boolean tryExecute()
    {
        return internalExecute(() -> {
            int rowsDeleted = context.deleteFrom(table)
                .where(getCombinedCondition())
                .execute();

            if (rowsDeleted == 0)
            {
                throwPermissionDeniedIfPrimaryKeyExists();

                // The desired record does not exist
                return false;
            }

            // The record existed, matched the denyUnless() condition [if one was given] and was now deleted.
            return true;
        });
    }

    @Override
    public Optional<P> tryExecuteAndConvert()
    {
        return internalExecute(() -> {
            Optional<P> result = context.deleteFrom(table)
                .where(getCombinedCondition())
                .returning()
                .fetchOptional()
                .map(converter::toPojo);

            if (result.isEmpty())
            {
                throwPermissionDeniedIfPrimaryKeyExists();
            }

            return result;
        });
    }

    @Override
    public void voidExecute()
    {
        tryExecute();
    }

    @Override
    public void withId(@NonNull Identifier<P> id)
    {
        TableField<R, String> idField = Tables.obtainSingleStringPrimaryKeyField(table);
        primaryKeyCondition = idField.eq(id.getValue());
    }
}
