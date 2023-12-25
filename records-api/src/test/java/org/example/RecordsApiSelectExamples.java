package org.example;

import static org.example.tables.Foo.FOO;

import java.util.Optional;
import java.util.stream.Stream;

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import org.example.business.Foo;
import org.example.store.FooRecordConverter;
import org.jooq.Field;

import com.github.bannmann.labs.records_api.Records;
import com.github.mizool.core.Identifier;

/**
 * Serves as a compile-time only verification of the capabilities of the records API.
 */
@SuppressWarnings("unused")
@RequiredArgsConstructor(onConstructor_ = @Inject, access = AccessLevel.PROTECTED)
public class RecordsApiSelectExamples
{
    private final Records records;

    public Optional<Foo> simpleRead(FooRecordConverter converter, Identifier<Foo> id)
    {
        return records.selectFrom(FOO)
            .convertedUsing(converter)
            .read(id)
            .fetchOptional();
    }

    public Optional<Foo> readVia(FooRecordConverter converter, Identifier<Foo> id)
    {
        return records.selectFrom(FOO)
            .convertedVia(converter::toPojo)
            .read(id)
            .fetchOptional();
    }

    public Optional<Foo> conditionalRead(FooRecordConverter converter, Identifier<Foo> id)
    {
        return records.selectFrom(FOO)
            .convertedUsing(converter)
            .read(id)
            .filter(FOO.BOOLEAN_DATA.isTrue())
            .fetchOptional();
    }

    public Optional<Foo> findBy(FooRecordConverter converter, Field<String> searchString)
    {
        return records.selectFrom(FOO)
            .convertedUsing(converter)
            .findWhere(FOO.TEXT_DATA.eq(searchString))
            .fetchOptional();
    }

    public Optional<Foo> findByVia(FooRecordConverter converter, Field<String> searchString)
    {
        return records.selectFrom(FOO)
            .convertedVia(converter::toPojo)
            .findWhere(FOO.TEXT_DATA.eq(searchString))
            .fetchOptional();
    }

    public Stream<Foo> simpleList(FooRecordConverter converter)
    {
        return records.selectFrom(FOO)
            .convertedUsing(converter)
            .list()
            .fetchStream();
    }

    public Stream<Foo> listVia(FooRecordConverter converter)
    {
        return records.selectFrom(FOO)
            .convertedVia(converter::toPojo)
            .list()
            .fetchStream();
    }

    public Stream<Foo> filteredList(FooRecordConverter converter)
    {
        return records.selectFrom(FOO)
            .convertedUsing(converter)
            .list()
            .filter(FOO.BOOLEAN_DATA.isTrue())
            .fetchStream();
    }

    public Stream<Foo> orderedList(FooRecordConverter converter)
    {
        return records.selectFrom(FOO)
            .convertedUsing(converter)
            .list()
            .orderBy(FOO.TEXT_DATA)
            .fetchStream();
    }

    public Stream<Foo> filteredOrderedList(FooRecordConverter converter)
    {
        return records.selectFrom(FOO)
            .convertedUsing(converter)
            .list()
            .filter(FOO.BOOLEAN_DATA.isTrue())
            .orderBy(FOO.TEXT_DATA)
            .fetchStream();
    }
}
