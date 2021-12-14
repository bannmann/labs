package org.example;

import static org.example.tables.Bar.BAR;
import static org.example.tables.Corge.CORGE;
import static org.example.tables.Fizzle.FIZZLE;
import static org.example.tables.Foo.FOO;
import static org.example.tables.Thud.THUD;

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import org.example.business.Bar;
import org.example.business.Corge;
import org.example.business.Fizzle;
import org.example.business.Foo;
import org.example.business.Thud;
import org.example.store.BarRecordConverter;
import org.example.store.CorgeRecordConverter;
import org.example.store.FizzleRecordConverter;
import org.example.store.FooRecordConverter;
import org.example.store.ThudRecordConverter;

import com.github.bannmann.labs.records_api.manual.Records;
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
            .withAnonymousConvertedVia(converter::fromPojo)
            .fromPojo(pojo)
            .executeAndConvert(converter::toPojo);
    }

    public Thud anonymousCreateWithTimestampAndEmail(ThudRecordConverter converter, Thud pojo)
    {
        return records.insertInto(THUD)
            .withAnonymousConvertedVia(converter::fromPojo)
            .fromPojo(pojo)
            .generating(THUD.TIMESTAMP)
            .normalizingEmail(THUD.TEXT_DATA)
            .executeAndConvert(converter::toPojo);
    }

    public Fizzle simpleCreate(FizzleRecordConverter converter, Fizzle pojo)
    {
        return records.insertInto(FIZZLE)
            .withIdentifiableConvertedVia(converter::fromPojo)
            .fromPojo(pojo)
            .executeAndConvert(converter::toPojo);
    }

    public Fizzle createWithEmail(FizzleRecordConverter converter, Fizzle pojo)
    {
        return records.insertInto(FIZZLE)
            .withIdentifiableConvertedVia(converter::fromPojo)
            .fromPojo(pojo)
            .normalizingEmail(FIZZLE.TEXT_DATA)
            .executeAndConvert(converter::toPojo);
    }

    public Bar createWithTimestamp(BarRecordConverter converter, Bar pojo)
    {
        return records.insertInto(BAR)
            .withIdentifiableConvertedVia(converter::fromPojo)
            .fromPojo(pojo)
            .generating(BAR.TIMESTAMP)
            .executeAndConvert(converter::toPojo);
    }

    public Bar createWithTimestampAndEmail(BarRecordConverter converter, Bar pojo)
    {
        return records.insertInto(BAR)
            .withIdentifiableConvertedVia(converter::fromPojo)
            .fromPojo(pojo)
            .generating(BAR.TIMESTAMP)
            .normalizingEmail(BAR.TEXT_DATA)
            .executeAndConvert(converter::toPojo);
    }

    public Fizzle unconditionalUpdate(FizzleRecordConverter converter, Fizzle newPojo)
    {
        return records.update(FIZZLE)
            .withRecordConvertedVia(converter::fromPojo)
            .fromNewPojo(newPojo)
            .executeAndConvert(converter::toPojo);
    }

    public Bar timestampedUpdate(BarRecordConverter converter, Bar newPojo, Bar existingPojo)
    {
        return records.update(BAR)
            .withRecordConvertedVia(converter::fromPojo)
            .fromNewPojo(newPojo)
            .checkAndRefresh(BAR.TIMESTAMP)
            .normalizingEmail(BAR.TEXT_DATA)
            .executeAndConvert(converter::toPojo);
    }

    public Foo versionedUpdate(FooRecordConverter converter, Foo newPojo)
    {
        return records.update(FOO)
            .withRecordConvertedVia(converter::fromPojo)
            .fromNewPojo(newPojo)
            .checkAndIncrease(FOO.VERSION)
            .executeAndConvert(converter::toPojo);
    }

    public Corge taggedUpdate(CorgeRecordConverter converter, Corge newPojo)
    {
        return records.update(CORGE)
            .withRecordConvertedVia(converter::fromPojo)
            .fromNewPojo(newPojo)
            .checkAndRandomize(CORGE.TAG, this::newTag)
            .executeAndConvert(converter::toPojo);
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
            .withRecordConvertedVia(converter::fromPojo)
            .fromNewPojo(newPojo)
            .postdetectCollisionIf(FIZZLE.BOOLEAN_DATA.ne(false), FIZZLE.BOOLEAN_DATA)
            .executeAndConvert(converter::toPojo);
    }

    public Bar refreshWithPostdetectUpdate(BarRecordConverter converter, Bar newPojo, Bar existingPojo)
    {
        return records.update(BAR)
            .withRecordConvertedVia(converter::fromPojo)
            .fromNewPojo(newPojo)
            .checkAndRefresh(BAR.TIMESTAMP)
            .postdetectCollisionIf(BAR.BOOLEAN_DATA.ne(false), BAR.BOOLEAN_DATA)
            .executeAndConvert(converter::toPojo);
    }

    public Fizzle withExistingPojo(FizzleRecordConverter converter, Fizzle newPojo, Fizzle existingPojo)
    {
        return records.update(FIZZLE)
            .withRecordConvertedVia(converter::fromPojo)
            .fromNewPojo(newPojo)
            .andExistingPojo(existingPojo)
            .executeAndConvert(converter::toPojo);
    }

    public Bar refreshWithExistingPojo(BarRecordConverter converter, Bar newPojo, Bar existingPojo)
    {
        return records.update(BAR)
            .withRecordConvertedVia(converter::fromPojo)
            .fromNewPojo(newPojo)
            .andExistingPojo(existingPojo)
            .checkAndRefresh(BAR.TIMESTAMP)
            .executeAndConvert(converter::toPojo);
    }

    public Foo increaseWithExistingPojo(FooRecordConverter converter, Foo newPojo, Foo existingPojo)
    {
        return records.update(FOO)
            .withRecordConvertedVia(converter::fromPojo)
            .fromNewPojo(newPojo)
            .andExistingPojo(existingPojo)
            .checkAndIncrease(FOO.VERSION)
            .executeAndConvert(converter::toPojo);
    }

    public Corge randomizeWithExistingPojo(CorgeRecordConverter converter, Corge newPojo, Corge existingPojo)
    {
        return records.update(CORGE)
            .withRecordConvertedVia(converter::fromPojo)
            .fromNewPojo(newPojo)
            .andExistingPojo(existingPojo)
            .checkAndRandomize(CORGE.TAG, this::newTag)
            .executeAndConvert(converter::toPojo);
    }

    public Bar refreshAndPredetectWithExistingPojo(BarRecordConverter converter, Bar newPojo, Bar existingPojo)
    {
        return records.update(BAR)
            .withRecordConvertedVia(converter::fromPojo)
            .fromNewPojo(newPojo)
            .andExistingPojo(existingPojo)
            .checkAndRefresh(BAR.TIMESTAMP)
            .predetectCollisionOn(BAR.TEXT_DATA)
            .executeAndConvert(converter::toPojo);
    }

    public Bar predetectAndRefreshWithExistingPojo(BarRecordConverter converter, Bar newPojo, Bar existingPojo)
    {
        return records.update(BAR)
            .withRecordConvertedVia(converter::fromPojo)
            .fromNewPojo(newPojo)
            .andExistingPojo(existingPojo)
            .predetectCollisionOn(BAR.TEXT_DATA)
            .checkAndRefresh(BAR.TIMESTAMP)
            .executeAndConvert(converter::toPojo);
    }

    public Foo predetectWithExistingPojo(FooRecordConverter converter, Foo newPojo, Foo existingPojo)
    {
        return records.update(FOO)
            .withRecordConvertedVia(converter::fromPojo)
            .fromNewPojo(newPojo)
            .andExistingPojo(existingPojo)
            .predetectCollisionOn(FOO.VERSION)
            .executeAndConvert(converter::toPojo);
    }

    public Thud anonymousRefresh(ThudRecordConverter converter, Thud newPojo, Thud existingPojo)
    {
        return records.update(THUD)
            .withRecordConvertedVia(converter::fromPojo)
            .fromNewPojo(newPojo)
            .checkAndRefresh(THUD.TIMESTAMP)
            .executeAndConvert(converter::toPojo);
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
}
