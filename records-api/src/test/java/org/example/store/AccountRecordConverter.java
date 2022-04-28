package org.example.store;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import org.example.business.Account;
import org.example.business.SingleSignOnPrincipal;
import org.example.tables.records.AccountRecord;

import com.github.bannmann.labs.records_api.RecordConverter;
import com.github.mizool.core.converter.IdentifierConverter;
import com.google.common.annotations.VisibleForTesting;

@VisibleForTesting
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class AccountRecordConverter implements RecordConverter<Account, AccountRecord>
{
    private final IdentifierConverter identifierConverter;

    public AccountRecord fromPojo(Account pojo)
    {
        AccountRecord result = null;

        if (pojo != null)
        {
            result = new AccountRecord();
            result.setId(identifierConverter.fromPojo(pojo.getId()));
            result.setDisplayName(pojo.getDisplayName());
            result.setEmail(pojo.getEmail());
            result.setSsoId(identifierConverter.fromPojo(pojo.getSsoId()));
            result.setTimestamp(pojo.getTimestamp());
        }

        return result;
    }

    @SuppressWarnings("java:S6213")
    public Account toPojo(AccountRecord record)
    {
        Account result = null;

        if (record != null)
        {
            result = Account.builder()
                .id(identifierConverter.toPojo(record.getId(), Account.class))
                .displayName(record.getDisplayName())
                .email(record.getEmail())
                .ssoId(identifierConverter.toPojo(record.getSsoId(), SingleSignOnPrincipal.class))
                .timestamp(record.getTimestamp())
                .build();
        }
        return result;
    }
}
