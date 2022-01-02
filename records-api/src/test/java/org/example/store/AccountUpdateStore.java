package org.example.store;

import static org.example.Tables.ACCOUNT;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import org.example.business.Account;

import com.github.bannmann.labs.records_api.Records;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class AccountUpdateStore
{
    private final AccountRecordConverter converter;
    private final Records records;

    public Account update(Account pojo)
    {
        return records.update(ACCOUNT)
            .withRecordConvertedVia(converter::fromPojo)
            .fromNewPojo(pojo)
            .checkAndRefresh(ACCOUNT.TIMESTAMP)
            .verifyUnchanged(ACCOUNT.EMAIL)
            .verifyUnchanged(ACCOUNT.SSO_ID)
            .executeAndConvert(converter::toPojo);
    }

    public Account update(Account pojo, Account existingPojo)
    {
        return records.update(ACCOUNT)
            .withRecordConvertedVia(converter::fromPojo)
            .fromNewPojo(pojo)
            .andExistingPojo(existingPojo)
            .checkAndRefresh(ACCOUNT.TIMESTAMP)
            .verifyUnchanged(ACCOUNT.EMAIL)
            .verifyUnchanged(ACCOUNT.SSO_ID)
            .executeAndConvert(converter::toPojo);
    }
}
