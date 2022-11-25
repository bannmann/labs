package org.example;

import static org.example.tables.Bar.BAR;
import static org.example.tables.Corge.CORGE;
import static org.example.tables.Fizzle.FIZZLE;
import static org.example.tables.Foo.FOO;
import static org.example.tables.Quux.QUUX;
import static org.example.tables.Thud.THUD;

import java.util.Optional;
import java.util.stream.Stream;

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import org.example.business.Bar;
import org.example.business.Corge;
import org.example.business.Fizzle;
import org.example.business.Foo;
import org.example.business.Quux;
import org.example.business.Thud;
import org.example.store.BarRecordConverter;
import org.example.store.CorgeRecordConverter;
import org.example.store.FizzleRecordConverter;
import org.example.store.FooRecordConverter;
import org.example.store.QuuxRecordConverter;
import org.example.store.ThudRecordConverter;
import org.example.tables.records.FooRecord;
import org.jooq.Field;
import org.jooq.ResultQuery;
import org.jooq.impl.DSL;

import com.github.bannmann.labs.records_api.Records;
import com.github.mizool.core.Identifier;

/**
 * Serves as a compile-time only verification of the capabilities of the records API.
 */
@SuppressWarnings("unused")
@RequiredArgsConstructor(onConstructor = @__(@Inject), access = AccessLevel.PROTECTED)
public class RecordsApiExamples
{
    private final Records records;

    public Thud simpleAnonymousCreate(ThudRecordConverter converter, Thud pojo)
    {
        return records.insertInto(THUD)
            .withAnonymousConvertedUsing(converter)
            .fromPojo(pojo)
            .executeAndConvert();
    }

    public Thud simpleAnonymousCreateWithLambdas(ThudRecordConverter converter, Thud pojo)
    {
        return records.insertInto(THUD)
            .withAnonymousConvertedVia(converter::fromPojo)
            .fromPojo(pojo)
            .executeAndConvertVia(converter::toPojo);
    }

    public Thud anonymousCreateWithTimestampAndEmail(ThudRecordConverter converter, Thud pojo)
    {
        return records.insertInto(THUD)
            .withAnonymousConvertedUsing(converter)
            .fromPojo(pojo)
            .generating(THUD.TIMESTAMP)
            .normalizingEmail(THUD.TEXT_DATA)
            .executeAndConvert();
    }

    public Fizzle simpleCreate(FizzleRecordConverter converter, Fizzle pojo)
    {
        return records.insertInto(FIZZLE)
            .withIdentifiableConvertedUsing(converter)
            .fromPojo(pojo)
            .executeAndConvert();
    }

    public Fizzle simpleCreateWithLambdas(FizzleRecordConverter converter, Fizzle pojo)
    {
        return records.insertInto(FIZZLE)
            .withIdentifiableConvertedVia(converter::fromPojo)
            .fromPojo(pojo)
            .executeAndConvertVia(converter::toPojo);
    }

    public Fizzle createWithEmail(FizzleRecordConverter converter, Fizzle pojo)
    {
        return records.insertInto(FIZZLE)
            .withIdentifiableConvertedUsing(converter)
            .fromPojo(pojo)
            .normalizingEmail(FIZZLE.TEXT_DATA)
            .executeAndConvert();
    }

    public Bar createWithTimestamp(BarRecordConverter converter, Bar pojo)
    {
        return records.insertInto(BAR)
            .withIdentifiableConvertedUsing(converter)
            .fromPojo(pojo)
            .generating(BAR.TIMESTAMP)
            .executeAndConvert();
    }

    public Bar createWithTimestampAndEmail(BarRecordConverter converter, Bar pojo)
    {
        return records.insertInto(BAR)
            .withIdentifiableConvertedUsing(converter)
            .fromPojo(pojo)
            .generating(BAR.TIMESTAMP)
            .normalizingEmail(BAR.TEXT_DATA)
            .executeAndConvert();
    }

    public Fizzle unconditionalUpdate(FizzleRecordConverter converter, Fizzle newPojo)
    {
        return records.update(FIZZLE)
            .withRecordConvertedUsing(converter)
            .fromNewPojo(newPojo)
            .executeAndConvert();
    }

    public Fizzle unconditionalUpdateWithLambdas(FizzleRecordConverter converter, Fizzle newPojo)
    {
        return records.update(FIZZLE)
            .withRecordConvertedVia(converter::fromPojo)
            .fromNewPojo(newPojo)
            .executeAndConvertVia(converter::toPojo);
    }

    public Bar timestampedUpdate(BarRecordConverter converter, Bar newPojo, Bar existingPojo)
    {
        return records.update(BAR)
            .withRecordConvertedUsing(converter)
            .fromNewPojo(newPojo)
            .checkAndRefresh(BAR.TIMESTAMP)
            .normalizingEmail(BAR.TEXT_DATA)
            .executeAndConvert();
    }

    public Foo versionedUpdate(FooRecordConverter converter, Foo newPojo)
    {
        return records.update(FOO)
            .withRecordConvertedUsing(converter)
            .fromNewPojo(newPojo)
            .checkAndIncrease(FOO.VERSION)
            .executeAndConvert();
    }

    public Corge taggedUpdate(CorgeRecordConverter converter, Corge newPojo)
    {
        return records.update(CORGE)
            .withRecordConvertedUsing(converter)
            .fromNewPojo(newPojo)
            .checkAndRandomize(CORGE.TAG, this::newTag)
            .executeAndConvert();
    }

    private String newTag()
    {
        return Identifier.forPojo(Object.class)
            .random()
            .getValue();
    }

    public Fizzle conditionalUpdate(FizzleRecordConverter converter, Fizzle newPojo)
    {
        return records.update(FIZZLE)
            .withRecordConvertedUsing(converter)
            .fromNewPojo(newPojo)
            .postdetectCollisionIf(FIZZLE.BOOLEAN_DATA.ne(false), FIZZLE.BOOLEAN_DATA)
            .executeAndConvert();
    }

    public Bar refreshWithPostdetectUpdate(BarRecordConverter converter, Bar newPojo, Bar existingPojo)
    {
        return records.update(BAR)
            .withRecordConvertedUsing(converter)
            .fromNewPojo(newPojo)
            .checkAndRefresh(BAR.TIMESTAMP)
            .postdetectCollisionIf(BAR.BOOLEAN_DATA.ne(false), BAR.BOOLEAN_DATA)
            .executeAndConvert();
    }

    public Fizzle withExistingPojo(FizzleRecordConverter converter, Fizzle newPojo, Fizzle existingPojo)
    {
        return records.update(FIZZLE)
            .withRecordConvertedUsing(converter)
            .fromNewPojo(newPojo)
            .andExistingPojo(existingPojo)
            .executeAndConvert();
    }

    public Bar refreshWithExistingPojo(BarRecordConverter converter, Bar newPojo, Bar existingPojo)
    {
        return records.update(BAR)
            .withRecordConvertedUsing(converter)
            .fromNewPojo(newPojo)
            .andExistingPojo(existingPojo)
            .checkAndRefresh(BAR.TIMESTAMP)
            .executeAndConvert();
    }

    public Foo increaseWithExistingPojo(FooRecordConverter converter, Foo newPojo, Foo existingPojo)
    {
        return records.update(FOO)
            .withRecordConvertedUsing(converter)
            .fromNewPojo(newPojo)
            .andExistingPojo(existingPojo)
            .checkAndIncrease(FOO.VERSION)
            .executeAndConvert();
    }

    public Corge randomizeWithExistingPojo(CorgeRecordConverter converter, Corge newPojo, Corge existingPojo)
    {
        return records.update(CORGE)
            .withRecordConvertedUsing(converter)
            .fromNewPojo(newPojo)
            .andExistingPojo(existingPojo)
            .checkAndRandomize(CORGE.TAG, this::newTag)
            .executeAndConvert();
    }

    public Bar refreshAndPredetectWithExistingPojo(BarRecordConverter converter, Bar newPojo, Bar existingPojo)
    {
        return records.update(BAR)
            .withRecordConvertedUsing(converter)
            .fromNewPojo(newPojo)
            .andExistingPojo(existingPojo)
            .checkAndRefresh(BAR.TIMESTAMP)
            .predetectCollisionOn(BAR.TEXT_DATA)
            .executeAndConvert();
    }

    public Bar predetectAndRefreshWithExistingPojo(BarRecordConverter converter, Bar newPojo, Bar existingPojo)
    {
        return records.update(BAR)
            .withRecordConvertedUsing(converter)
            .fromNewPojo(newPojo)
            .andExistingPojo(existingPojo)
            .predetectCollisionOn(BAR.TEXT_DATA)
            .checkAndRefresh(BAR.TIMESTAMP)
            .executeAndConvert();
    }

    public Foo predetectWithExistingPojo(FooRecordConverter converter, Foo newPojo, Foo existingPojo)
    {
        return records.update(FOO)
            .withRecordConvertedUsing(converter)
            .fromNewPojo(newPojo)
            .andExistingPojo(existingPojo)
            .predetectCollisionOn(FOO.VERSION)
            .executeAndConvert();
    }

    public Quux anonymousRefresh(QuuxRecordConverter converter, Quux newPojo, Quux existingPojo)
    {
        return records.update(QUUX)
            .withRecordConvertedUsing(converter)
            .fromNewPojo(newPojo)
            .checkAndRefresh(QUUX.UPDATED)
            .executeAndConvert();
    }

    public void unconditionalPartialUpdateA(Identifier<Fizzle> id)
    {
        records.update(FIZZLE)
            .withPrimaryKey(id, Fizzle.class)
            .set(FIZZLE.TEXT_DATA, "Hello, World!")
            .execute();
    }

    public void unconditionalPartialUpdateB(Identifier<Foo> id)
    {
        records.update(FOO)
            .withPrimaryKey(id, Foo.class)
            .set(FOO.TEXT_DATA, "{breaking news here}")
            .increase(FOO.VERSION)
            .execute();
    }

    public void unconditionalPartialUpdateC(Identifier<Bar> id)
    {
        records.update(BAR)
            .withPrimaryKey(id, Bar.class)
            .set(BAR.TEXT_DATA, "important")
            .refresh(BAR.TIMESTAMP)
            .execute();
    }

    public void conditionalPartialUpdate(Identifier<Fizzle> id)
    {
        records.update(FIZZLE)
            .withPrimaryKey(id, Fizzle.class)
            .set(FIZZLE.BOOLEAN_DATA, true)
            .postdetectCollisionIf(FIZZLE.BOOLEAN_DATA.ne(false), FIZZLE.BOOLEAN_DATA)
            .execute();
    }

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
