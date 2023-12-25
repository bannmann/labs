package org.example;

import java.util.Optional;
import java.util.stream.Stream;

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import org.example.business.Foo;
import org.example.store.FooRecordConverter;
import org.example.tables.records.FooRecord;
import org.jooq.ResultQuery;

import dev.bannmann.labs.records_api.Records;

/**
 * Serves as a compile-time only verification of the capabilities of the records API.
 */
@SuppressWarnings("unused")
@RequiredArgsConstructor(onConstructor_ = @Inject, access = AccessLevel.PROTECTED)
public class RecordsApiQueryExamples
{
    private final Records records;

    public Optional<Foo> queryOptional(FooRecordConverter converter, ResultQuery<FooRecord> resultQuery)
    {
        return records.query(resultQuery)
            .convertedUsing(converter)
            .fetchOptional();
    }

    public Optional<Foo> queryOptionalVia(FooRecordConverter converter, ResultQuery<FooRecord> resultQuery)
    {
        return records.query(resultQuery)
            .convertedVia(converter::toPojo)
            .fetchOptional();
    }

    public Stream<Foo> queryStream(FooRecordConverter converter, ResultQuery<FooRecord> resultQuery)
    {
        return records.query(resultQuery)
            .convertedUsing(converter)
            .fetchStream();
    }

    public Stream<Foo> queryStreamVia(FooRecordConverter converter, ResultQuery<FooRecord> resultQuery)
    {
        return records.query(resultQuery)
            .convertedVia(converter::toPojo)
            .fetchStream();
    }

    public Optional<FooRecord> queryRecordOptional(ResultQuery<FooRecord> resultQuery)
    {
        return records.query(resultQuery)
            .skipConversion()
            .fetchRecordOptional();
    }

    public Stream<FooRecord> queryRecordStream(ResultQuery<FooRecord> resultQuery)
    {
        return records.query(resultQuery)
            .skipConversion()
            .fetchRecordStream();
    }
}
