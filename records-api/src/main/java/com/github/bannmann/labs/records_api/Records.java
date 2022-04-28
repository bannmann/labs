package com.github.bannmann.labs.records_api;

import lombok.RequiredArgsConstructor;

import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.UpdatableRecord;

import com.github.bannmann.labs.annotations.UpstreamCandidate;
import com.github.bannmann.labs.records_api.state1.Insert;
import com.github.bannmann.labs.records_api.state1.Update;

@UpstreamCandidate("Mizool")
@RequiredArgsConstructor
public class Records
    implements com.github.bannmann.labs.records_api.IInsert, com.github.bannmann.labs.records_api.IUpdate
{
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
}
