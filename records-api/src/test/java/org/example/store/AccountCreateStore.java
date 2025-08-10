package org.example.store;

import static org.example.Tables.ACCOUNT;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import org.example.business.Account;

import com.google.common.annotations.VisibleForTesting;
import dev.bannmann.labs.records_api.Records;

@VisibleForTesting
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AccountCreateStore
{
    private final AccountRecordConverter converter;
    private final Records records;

    public Account create(Account pojo)
    {
        return records.insertInto(ACCOUNT)
            .withIdentifiableConvertedUsing(converter)
            .fromPojo(pojo)
            .requireNull(ACCOUNT.SSO_ID)
            .generating(ACCOUNT.TIMESTAMP)
            .normalizingEmail(ACCOUNT.EMAIL)
            .executeAndConvert();
    }
}
