package org.example.store;

import static org.example.Tables.ACCOUNT;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import org.example.business.Account;

import com.github.bannmann.labs.records_api.manual.Records;
import com.google.common.annotations.VisibleForTesting;

@VisibleForTesting
@RequiredArgsConstructor(onConstructor = @__(@Inject))
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
