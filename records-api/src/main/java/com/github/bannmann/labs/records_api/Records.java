package com.github.bannmann.labs.records_api;

import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.ResultQuery;
import org.jooq.Table;
import org.jooq.UpdatableRecord;
import org.jooq.exception.DataAccessException;

import com.github.bannmann.labs.annotations.UpstreamCandidate;
import com.github.bannmann.labs.records_api.state1.Insert;
import com.github.bannmann.labs.records_api.state1.Select;
import com.github.bannmann.labs.records_api.state1.Update;
import com.github.mizool.core.Identifiable;
import com.github.mizool.core.exception.StoreLayerException;

@UpstreamCandidate("Mizool")
@RequiredArgsConstructor
public class Records implements IInsert, IUpdate, ISelect
{
    @UtilityClass
    private static class DummyIdentifiable implements Identifiable<DummyIdentifiable>
    {
    }

    private final DSLContext context;
    private final StoreClock storeClock;

    @Override
    public <R extends UpdatableRecord<R>> Insert<R> insertInto(Table<R> table)
    {
        return new Insert0(new InsertActionImpl<Object, R>(context, storeClock)).insertInto(table);
    }

    @Override
    public <R extends UpdatableRecord<R>> Update<R> update(Table<R> table)
    {
        return new Update0(new UpdateActionImpl<Object, R>(context, storeClock)).update(table);
    }

    @Override
    public <R extends Record> com.github.bannmann.labs.records_api.state2.Select<R> selectFrom(Table<R> table)
    {
        return createSelect().selectFrom(table);
    }

    private <R extends Record> Select0 createSelect()
    {
        return new Select0(new SelectActionImpl<DummyIdentifiable, R>(context));
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
