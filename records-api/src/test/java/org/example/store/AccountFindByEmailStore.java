package org.example.store;

import static org.example.Tables.ACCOUNT;

import java.util.Locale;
import java.util.Optional;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import org.example.business.Account;

import com.github.bannmann.labs.records_api.Records;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AccountFindByEmailStore
{
    private final Records records;
    private final AccountRecordConverter converter;

    public Optional<Account> find(String email)
    {
        String normalizedEmail = email.toLowerCase(Locale.ROOT);

        return records.selectFrom(ACCOUNT)
            .convertedUsing(converter)
            .findWhere(ACCOUNT.EMAIL.eq(normalizedEmail))
            .fetchOptional();
    }
}
