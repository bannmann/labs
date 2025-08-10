package dev.bannmann.labs.records_api;

import java.util.Optional;
import java.util.function.Supplier;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UpdatableRecord;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jspecify.annotations.NullMarked;

import com.github.mizool.core.Identifier;
import com.github.mizool.core.exception.ObjectNotFoundException;
import com.github.mizool.core.exception.PermissionDeniedException;
import com.github.mizool.core.exception.StoreLayerException;
import dev.bannmann.labs.annotations.SuppressWarningsRationale;
import dev.bannmann.labs.core.Box;

@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("java:S2638")
@SuppressWarningsRationale(
    "The Silverchain-generated interface is not @NullMarked yet, so Sonar flags our use of lombok @NonNull on many parameters as a contract change.")
@NullMarked
class DeleteActionImpl<P, R extends UpdatableRecord<R>> implements IDeleteAction<P, R>
{
    private final Box<Table<R>> tableBox = new Box<>();
    private final Box<RecordConverter<P, R>> converterBox = new Box<>();
    private final Box<Condition> primaryKeyConditionBox = new Box<>();

    /**
     * Non-PK conditions which must hold for the deletion to happen.
     *
     * <p>The condition is added as-is to the {@code WHERE} clause. If no rows are deleted, the negated version of this
     * condition is used in a {@code SELECT} to distinguish "object not found" from "permission denied".
     */
    private final Box<Condition> deletionPermittedConditionBox = new Box<>();

    private final DSLContext context;

    @Override
    public void delete()
    {
        // Nothing to do here.
    }

    @Override
    public void denyUnless(@NonNull Condition condition)
    {
        deletionPermittedConditionBox.set(condition);
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
        tableBox.set(table);

        // Note: the converterClass argument only exists for type safety further down the chain
    }

    @Override
    public void fromIdentifiable(@NonNull Table<R> table, @NonNull RecordConverter<P, R> converter)
    {
        tableBox.set(table);
        converterBox.set(converter);
    }

    private Condition getCombinedCondition()
    {
        Condition first = primaryKeyConditionBox.get();

        Condition second = deletionPermittedConditionBox.getOrNull();
        if (second != null)
        {
            return first.and(second);
        }

        return first;
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
            throw new StoreLayerException("Error deleting from " +
                                          tableBox.get()
                                              .getName(), e);
        }
    }

    private void throwPermissionDeniedIfPrimaryKeyExists()
    {
        var table = tableBox.get();
        var primaryKeyCondition = primaryKeyConditionBox.get();

        if (context.fetchExists(DSL.selectFrom(table)
            .where(primaryKeyCondition)))
        {
            throw new PermissionDeniedException();
        }
    }

    @Override
    public boolean tryExecute()
    {
        var table = tableBox.get();

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
        var table = tableBox.get();
        var converter = converterBox.get();

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
        var table = tableBox.get();
        TableField<R, String> idField = Tables.obtainSingleStringPrimaryKeyField(table);
        primaryKeyConditionBox.set(idField.eq(id.getValue()));
    }
}
