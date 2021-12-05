package org.example;

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import org.example.tables.records.AccountRecord;

import com.github.mizool.core.converter.IdentifierConverter;

@RequiredArgsConstructor(onConstructor = @__(@Inject), access = AccessLevel.PROTECTED)
class AccountRecordConverter
{
    private final IdentifierConverter identifierConverter;

    public AccountRecord fromPojo(Account pojo)
    {
        AccountRecord result = null;

        if (pojo != null)
        {
            result = new AccountRecord();
            result.setId(identifierConverter.fromPojo(pojo.getId()));
            result.setEmail(pojo.getEmail());
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
                .email(record.getEmail())
                .timestamp(record.getTimestamp())
                .build();
        }
        return result;
    }
}
