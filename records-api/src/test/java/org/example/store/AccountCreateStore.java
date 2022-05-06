package org.example.store;

import static org.example.Tables.ACCOUNT;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import org.example.business.Account;

import com.github.bannmann.labs.records_api.Records;
import com.github.mizool.core.exception.ReadonlyFieldException;
import com.google.common.annotations.VisibleForTesting;

@VisibleForTesting
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class AccountCreateStore
{
    private final AccountRecordConverter converter;
    private final Records records;

    public Account create(Account pojo)
    {
        verifyNoSsoId(pojo);

        return records.insertInto(ACCOUNT)
            .withIdentifiableConvertedUsing(converter)
            .fromPojo(pojo)
            .generating(ACCOUNT.TIMESTAMP)
            .normalizingEmail(ACCOUNT.EMAIL)
            .executeAndConvert();
    }

    private void verifyNoSsoId(Account pojo)
    {
        if (pojo.getSsoId() != null)
        {
            throw new ReadonlyFieldException(Account.Fields.ssoId);
        }
    }
}
