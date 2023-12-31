package org.example;

import static org.example.Tables.FIZZLE;

import java.util.Optional;

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import org.example.business.Account;
import org.example.business.Fizzle;
import org.example.store.FizzleRecordConverter;

import com.github.mizool.core.Identifier;
import dev.bannmann.labs.records_api.Records;

/**
 * Serves as a compile-time only verification of the capabilities of the records API.
 */
@SuppressWarnings("unused")
@RequiredArgsConstructor(onConstructor_ = @Inject, access = AccessLevel.PROTECTED)
public class RecordsApiDeleteExamples
{
    private final Records records;

    public void deleteUsingConverterClass(Identifier<Fizzle> id)
    {
        records.delete()
            .fromIdentifiable(FIZZLE, FizzleRecordConverter.class)
            .withId(id)
            .voidExecute();
    }

    public boolean tryDeleteUsingConverterClass(Identifier<Fizzle> id)
    {
        return records.delete()
            .fromIdentifiable(FIZZLE, FizzleRecordConverter.class)
            .withId(id)
            .ignoreIfNotFound()
            .tryExecute();
    }

    public void deleteUsingConverterInstance(Identifier<Fizzle> id, FizzleRecordConverter converter)
    {
        records.delete()
            .fromIdentifiable(FIZZLE, converter)
            .withId(id)
            .voidExecute();
    }

    public boolean tryDeleteUsingConverterInstance(Identifier<Fizzle> id, FizzleRecordConverter converter)
    {
        return records.delete()
            .fromIdentifiable(FIZZLE, converter)
            .withId(id)
            .ignoreIfNotFound()
            .tryExecute();
    }

    public void deleteUsingConverterClassWithPermissionCheck(Identifier<Fizzle> id, String prerequisiteText)
    {
        records.delete()
            .fromIdentifiable(FIZZLE, FizzleRecordConverter.class)
            .withId(id)
            .denyUnless(FIZZLE.TEXT_DATA.eq(prerequisiteText))
            .voidExecute();
    }

    public boolean tryDeleteUsingConverterClassWithPermissionCheck(Identifier<Fizzle> id, String prerequisiteText)
    {
        return records.delete()
            .fromIdentifiable(FIZZLE, FizzleRecordConverter.class)
            .withId(id)
            .denyUnless(FIZZLE.TEXT_DATA.eq(prerequisiteText))
            .ignoreIfNotFound()
            .tryExecute();
    }

    public void deleteUsingConverterInstanceWithPermissionCheck(
        Identifier<Fizzle> id, FizzleRecordConverter converter, Identifier<Account> currentUser)
    {
        records.delete()
            .fromIdentifiable(FIZZLE, converter)
            .withId(id)
            .denyUnless(FIZZLE.TEXT_DATA.eq(currentUser.getValue()))
            .voidExecute();
    }

    public void tryDeleteUsingConverterInstanceWithPermissionCheck(
        Identifier<Fizzle> id, FizzleRecordConverter converter, Identifier<Account> currentUser)
    {
        records.delete()
            .fromIdentifiable(FIZZLE, converter)
            .withId(id)
            .denyUnless(FIZZLE.TEXT_DATA.eq(currentUser.getValue()))
            .ignoreIfNotFound()
            .tryExecute();
    }

    public Fizzle deleteAndReturnPojo(Identifier<Fizzle> id, FizzleRecordConverter converter)
    {
        return records.delete()
            .fromIdentifiable(FIZZLE, converter)
            .withId(id)
            .executeAndConvert();
    }

    public Optional<Fizzle> tryDeleteAndReturnPojo(Identifier<Fizzle> id, FizzleRecordConverter converter)
    {
        return records.delete()
            .fromIdentifiable(FIZZLE, converter)
            .withId(id)
            .ignoreIfNotFound()
            .tryExecuteAndConvert();
    }

    public Fizzle deleteWithPermissionCheckAndReturnPojo(
        Identifier<Fizzle> id, FizzleRecordConverter converter, Identifier<Account> currentUser)
    {
        return records.delete()
            .fromIdentifiable(FIZZLE, converter)
            .withId(id)
            .denyUnless(FIZZLE.TEXT_DATA.eq(currentUser.getValue()))
            .executeAndConvert();
    }

    public Optional<Fizzle> tryDeleteWithPermissionCheckAndReturnPojo(
        Identifier<Fizzle> id, FizzleRecordConverter converter, Identifier<Account> currentUser)
    {
        return records.delete()
            .fromIdentifiable(FIZZLE, converter)
            .withId(id)
            .denyUnless(FIZZLE.TEXT_DATA.eq(currentUser.getValue()))
            .ignoreIfNotFound()
            .tryExecuteAndConvert();
    }
}
