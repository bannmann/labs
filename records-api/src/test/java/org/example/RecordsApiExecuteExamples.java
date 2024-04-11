package org.example;

import static org.example.tables.Foo.FOO;

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import org.jooq.impl.DSL;

import dev.bannmann.labs.records_api.Records;

/**
 * Serves as a compile-time only verification of the capabilities of the records API.
 */
@SuppressWarnings("unused")
@RequiredArgsConstructor(onConstructor_ = @Inject, access = AccessLevel.PROTECTED)
public class RecordsApiExecuteExamples
{
    private final Records records;

    public int execute()
    {
        return records.execute(DSL.update(FOO)
            .set(FOO.TEXT_DATA, "Bar"));
    }

    public int executeOnContext()
    {
        return records.execute(dslContext -> dslContext.update(FOO)
            .set(FOO.TEXT_DATA, "Bar"));
    }
}
