package org.example;

import static org.example.tables.Foo.FOO;

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import org.jooq.impl.DSL;

import com.github.bannmann.labs.records_api.Records;

/**
 * Serves as a compile-time only verification of the capabilities of the records API.
 */
@SuppressWarnings("unused")
@RequiredArgsConstructor(onConstructor_ = @Inject, access = AccessLevel.PROTECTED)
public class RecordsApiExecuteExamples
{
    private final Records records;

    public void execute()
    {
        records.execute(DSL.dropTable(FOO));
    }

    public void executeOnContext()
    {
        records.execute(dslContext -> dslContext.alterTable(FOO)
            .dropColumn(FOO.BOOLEAN_DATA));
    }
}
