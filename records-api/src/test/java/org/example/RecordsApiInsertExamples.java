package org.example;

import static org.example.tables.Bar.BAR;
import static org.example.tables.Fizzle.FIZZLE;
import static org.example.tables.Thud.THUD;

import java.util.List;

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import org.example.business.Bar;
import org.example.business.Fizzle;
import org.example.business.Thud;
import org.example.store.BarRecordConverter;
import org.example.store.FizzleRecordConverter;
import org.example.store.ThudRecordConverter;

import dev.bannmann.labs.records_api.Records;

/**
 * Serves as a compile-time only verification of the capabilities of the records API.
 */
@SuppressWarnings("unused")
@RequiredArgsConstructor(onConstructor_ = @Inject, access = AccessLevel.PROTECTED)
public class RecordsApiInsertExamples
{
    private final Records records;

    public Fizzle simpleCreate(FizzleRecordConverter converter, Fizzle pojo)
    {
        return records.insertInto(FIZZLE)
            .withIdentifiableConvertedUsing(converter)
            .fromPojo(pojo)
            .executeAndConvert();
    }

    public Fizzle simpleCreateWithId(FizzleRecordConverter converter, Fizzle pojo)
    {
        return records.insertInto(FIZZLE)
            .withIdentifiableConvertedUsing(converter)
            .fromPojoWithPresetId(pojo)
            .executeAndConvert();
    }

    public Fizzle simpleCreateWithIdIfNotExist(FizzleRecordConverter converter, Fizzle pojo)
    {
        return records.insertInto(FIZZLE)
            .withIdentifiableConvertedUsing(converter)
            .fromPojoWithPresetId(pojo)
            .onDuplicateKeyIgnore()
            .executeAndConvert();
    }

    public void simpleCreateDiscardingResult(FizzleRecordConverter converter, Fizzle pojo)
    {
        records.insertInto(FIZZLE)
            .withIdentifiableConvertedUsing(converter)
            .fromPojo(pojo)
            .voidExecute();
    }

    public Fizzle simpleCreateWithLambda(FizzleRecordConverter converter, Fizzle pojo)
    {
        return records.insertInto(FIZZLE)
            .withIdentifiableConvertedVia(converter::fromPojo)
            .fromPojo(pojo)
            .executeAndConvertVia(converter::toPojo);
    }

    public void simpleCreateWithLambdaDiscardingResult(FizzleRecordConverter converter, Fizzle pojo)
    {
        records.insertInto(FIZZLE)
            .withIdentifiableConvertedVia(converter::fromPojo)
            .fromPojo(pojo)
            .voidExecute();
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

    public void batchCreate(FizzleRecordConverter converter, List<Fizzle> pojos)
    {
        records.insertInto(FIZZLE)
            .withIdentifiableConvertedUsing(converter)
            .fromPojos(pojos)
            .voidExecute();
    }

    public void batchCreateWithLambda(FizzleRecordConverter converter, List<Fizzle> pojos)
    {
        records.insertInto(FIZZLE)
            .withIdentifiableConvertedVia(converter::fromPojo)
            .fromPojos(pojos)
            .voidExecute();
    }

    public void batchCreateWithTimestampAndEmail(BarRecordConverter converter, List<Bar> pojos)
    {
        records.insertInto(BAR)
            .withIdentifiableConvertedUsing(converter)
            .fromPojos(pojos)
            .generating(BAR.TIMESTAMP)
            .normalizingEmail(BAR.TEXT_DATA)
            .voidExecute();
    }

    public void batchCreateWithId(FizzleRecordConverter converter, List<Fizzle> pojos)
    {
        records.insertInto(FIZZLE)
            .withIdentifiableConvertedUsing(converter)
            .fromPojosWithPresetId(pojos)
            .voidExecute();
    }

    public Thud simpleCustomKeyedCreate(ThudRecordConverter converter, Thud pojo)
    {
        return records.insertInto(THUD)
            .withCustomKeyedConvertedUsing(converter)
            .fromPojo(pojo)
            .executeAndConvert();
    }

    public Thud simpleCustomKeyedCreateIfNotExist(ThudRecordConverter converter, Thud pojo)
    {
        return records.insertInto(THUD)
            .withCustomKeyedConvertedUsing(converter)
            .fromPojo(pojo)
            .onDuplicateKeyIgnore()
            .executeAndConvert();
    }

    public void simpleCustomKeyedCreateDiscardingResult(ThudRecordConverter converter, Thud pojo)
    {
        records.insertInto(THUD)
            .withCustomKeyedConvertedUsing(converter)
            .fromPojo(pojo)
            .voidExecute();
    }

    public Thud simpleCustomKeyedCreateWithLambda(ThudRecordConverter converter, Thud pojo)
    {
        return records.insertInto(THUD)
            .withCustomKeyedConvertedVia(converter::fromPojo)
            .fromPojo(pojo)
            .executeAndConvertVia(converter::toPojo);
    }

    public Thud simpleCustomKeyedCreateWithLambdaDiscardingResult(ThudRecordConverter converter, Thud pojo)
    {
        return records.insertInto(THUD)
            .withCustomKeyedConvertedVia(converter::fromPojo)
            .fromPojo(pojo)
            .executeAndConvertVia(converter::toPojo);
    }

    public Thud customKeyedCreateWithTimestampAndEmail(ThudRecordConverter converter, Thud pojo)
    {
        return records.insertInto(THUD)
            .withCustomKeyedConvertedUsing(converter)
            .fromPojo(pojo)
            .generating(THUD.TIMESTAMP)
            .normalizingEmail(THUD.TEXT_DATA)
            .executeAndConvert();
    }

    public void customKeyedBatchCreate(ThudRecordConverter converter, List<Thud> pojos)
    {
        records.insertInto(THUD)
            .withCustomKeyedConvertedUsing(converter)
            .fromPojos(pojos)
            .voidExecute();
    }

    public void customKeyedBatchCreateWithLambda(ThudRecordConverter converter, List<Thud> pojos)
    {
        records.insertInto(THUD)
            .withCustomKeyedConvertedVia(converter::fromPojo)
            .fromPojos(pojos)
            .voidExecute();
    }

    public void customKeyedBatchCreateWithTimestampAndEmail(ThudRecordConverter converter, List<Thud> pojos)
    {
        records.insertInto(THUD)
            .withCustomKeyedConvertedUsing(converter)
            .fromPojos(pojos)
            .generating(THUD.TIMESTAMP)
            .normalizingEmail(THUD.TEXT_DATA)
            .voidExecute();
    }
}
