package org.example;

import static org.example.tables.Bar.BAR;
import static org.example.tables.Corge.CORGE;
import static org.example.tables.Fizzle.FIZZLE;
import static org.example.tables.Foo.FOO;
import static org.example.tables.Quux.QUUX;

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import org.example.business.Bar;
import org.example.business.Corge;
import org.example.business.Fizzle;
import org.example.business.Foo;
import org.example.business.Quux;
import org.example.store.BarRecordConverter;
import org.example.store.CorgeRecordConverter;
import org.example.store.FizzleRecordConverter;
import org.example.store.FooRecordConverter;
import org.example.store.QuuxRecordConverter;

import com.github.bannmann.labs.records_api.Records;
import com.github.mizool.core.Identifier;

/**
 * Serves as a compile-time only verification of the capabilities of the records API.
 */
@SuppressWarnings("unused")
@RequiredArgsConstructor(onConstructor_ = @Inject, access = AccessLevel.PROTECTED)
public class RecordsApiUpdateExamples
{
    private final Records records;

    public Fizzle unconditionalUpdate(FizzleRecordConverter converter, Fizzle newPojo)
    {
        return records.update(FIZZLE)
            .withRecordConvertedUsing(converter)
            .fromNewPojo(newPojo)
            .executeAndConvert();
    }

    public void unconditionalUpdateDiscardingResult(FizzleRecordConverter converter, Fizzle newPojo)
    {
        records.update(FIZZLE)
            .withRecordConvertedUsing(converter)
            .fromNewPojo(newPojo)
            .voidExecute();
    }

    public Fizzle unconditionalUpdateWithLambdas(FizzleRecordConverter converter, Fizzle newPojo)
    {
        return records.update(FIZZLE)
            .withRecordConvertedVia(converter::fromPojo)
            .fromNewPojo(newPojo)
            .executeAndConvertVia(converter::toPojo);
    }

    public void unconditionalUpdateWithLambdaDiscardingResult(FizzleRecordConverter converter, Fizzle newPojo)
    {
        records.update(FIZZLE)
            .withRecordConvertedVia(converter::fromPojo)
            .fromNewPojo(newPojo)
            .voidExecute();
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

    public void refreshAndPredetectWithExistingPojoDiscardingResult(
        BarRecordConverter converter, Bar newPojo, Bar existingPojo)
    {
        records.update(BAR)
            .withRecordConvertedUsing(converter)
            .fromNewPojo(newPojo)
            .andExistingPojo(existingPojo)
            .checkAndRefresh(BAR.TIMESTAMP)
            .predetectCollisionOn(BAR.TEXT_DATA)
            .voidExecute();
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

    public Quux customKeyedRefresh(QuuxRecordConverter converter, Quux newPojo, Quux existingPojo)
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
            .voidExecute();
    }

    public void unconditionalPartialUpdateB(Identifier<Foo> id)
    {
        records.update(FOO)
            .withPrimaryKey(id, Foo.class)
            .set(FOO.TEXT_DATA, "{breaking news here}")
            .increase(FOO.VERSION)
            .voidExecute();
    }

    public void unconditionalPartialUpdateC(Identifier<Bar> id)
    {
        records.update(BAR)
            .withPrimaryKey(id, Bar.class)
            .set(BAR.TEXT_DATA, "important")
            .refresh(BAR.TIMESTAMP)
            .voidExecute();
    }

    public void conditionalPartialUpdate(Identifier<Fizzle> id)
    {
        records.update(FIZZLE)
            .withPrimaryKey(id, Fizzle.class)
            .set(FIZZLE.BOOLEAN_DATA, true)
            .postdetectCollisionIf(FIZZLE.BOOLEAN_DATA.ne(false), FIZZLE.BOOLEAN_DATA)
            .voidExecute();
    }
}
