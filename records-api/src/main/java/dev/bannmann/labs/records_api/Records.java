package dev.bannmann.labs.records_api;

import java.util.function.Function;

import lombok.RequiredArgsConstructor;

import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.ResultQuery;
import org.jooq.Table;
import org.jooq.UpdatableRecord;
import org.jooq.exception.DataAccessException;

import com.github.mizool.core.exception.StoreLayerException;
import dev.bannmann.labs.annotations.UpstreamCandidate;
import dev.bannmann.labs.records_api.state1.Delete;
import dev.bannmann.labs.records_api.state1.Insert;
import dev.bannmann.labs.records_api.state1.Select;
import dev.bannmann.labs.records_api.state1.Update;

@UpstreamCandidate("Mizool")
@RequiredArgsConstructor
public class Records implements IInsert, IUpdate, ISelect, IDelete
{
    private final DSLContext context;
    private final StoreClock storeClock;

    @Override
    public Delete delete()
    {
        var action = new DeleteActionImpl<>(context);
        return new Delete0(action).delete();
    }

    @Override
    public <R extends UpdatableRecord<R>> Insert<R> insertInto(Table<R> table)
    {
        var action = new InsertActionImpl<>(context, storeClock);
        return new Insert0(action).insertInto(table);
    }

    @Override
    public <R extends UpdatableRecord<R>> Update<R> update(Table<R> table)
    {
        var action = new UpdateActionImpl<>(context, storeClock);
        return new Update0(action).update(table);
    }

    @Override
    public <R extends Record> dev.bannmann.labs.records_api.state2.Select<R> selectFrom(Table<R> table)
    {
        return createSelect().selectFrom(table);
    }

    private Select0 createSelect()
    {
        var action = new SelectActionImpl<>(context);
        return new Select0(action);
    }

    @Override
    public <R extends Record> Select<R> query(ResultQuery<R> resultQuery)
    {
        resultQuery.attach(context.configuration());
        return createSelect().query(resultQuery);
    }

    public void execute(Query query)
    {
        wrapExecution(() -> context.execute(query));
    }

    private void wrapExecution(Runnable execution)
    {
        try
        {
            execution.run();
        }
        catch (DataAccessException e)
        {
            throw new StoreLayerException(e.getMessage());
        }
    }

    public void execute(Function<DSLContext, Query> queryFactory)
    {
        wrapExecution(() -> queryFactory.apply(context)
            .execute());
    }
}
