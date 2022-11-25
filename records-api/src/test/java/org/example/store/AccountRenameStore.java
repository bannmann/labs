package org.example.store;

import static org.example.Tables.ACCOUNT;

import java.util.Optional;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import org.example.business.Account;
import org.jooq.DSLContext;

import com.github.bannmann.labs.records_api.Records;
import com.github.mizool.core.Identifier;
import com.github.mizool.core.Optionals;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AccountRenameStore
{
    private final AccountRecordConverter converter;
    private final Records records;
    private final DSLContext dslContext;

    public void rename(Identifier<Account> id, String newDisplayName)
    {
        Optional<Account> accountOptional = dslContext.selectFrom(ACCOUNT)
            .where(ACCOUNT.ID.eq(id.getValue()))
            .fetchOptional()
            .map(converter::toPojo);

        Account existingAccount = Optionals.unwrapUserRequestedObject(accountOptional, Account.class);
        Account account = existingAccount.toBuilder()
            .displayName(newDisplayName)
            .build();

        records.update(ACCOUNT)
            .withRecordConvertedUsing(converter)
            .fromNewPojo(account)
            .andExistingPojo(existingAccount)
            .checkAndRefresh(ACCOUNT.TIMESTAMP)
            .checkAndIncrease(ACCOUNT.RENAME_COUNT)
            .executeAndConvert();
    }
}
