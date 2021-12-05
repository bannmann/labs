package org.example;

import static org.example.Tables.ACCOUNT;

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import com.github.bannmann.labs.records_api.manual.Records;

@RequiredArgsConstructor(onConstructor = @__(@Inject), access = AccessLevel.PROTECTED)
public class ManualRecordsApiVersionOfAccountCreateStore
{
    private final AccountRecordConverter converter;
    private final Records records;

    public Account create(Account pojo)
    {
        return records.insertInto(ACCOUNT)
            .withIdentifiableConvertedVia(converter::fromPojo)
            .fromPojo(pojo)
            .generating(ACCOUNT.TIMESTAMP)
            .normalizingEmail(ACCOUNT.EMAIL)
            .executeAndConvert(converter::toPojo);
    }
}
