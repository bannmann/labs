package org.example.store;

import static org.example.Tables.ACCOUNT;

import java.util.Optional;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import org.example.business.Account;

import com.github.mizool.core.Identifier;
import dev.bannmann.labs.records_api.Records;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AccountReadStore
{
    private final Records records;
    private final AccountRecordConverter converter;

    public Optional<Account> read(Identifier<Account> id)
    {
        return records.selectFrom(ACCOUNT)
            .convertedUsing(converter)
            .read(id)
            .fetchOptional();
    }
}
