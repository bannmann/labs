package org.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.Tables.ACCOUNT;

import org.example.business.Account;
import org.example.store.AccountCreateStore;
import org.example.store.AccountRenameStore;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class TestAccountRenaming extends AbstractRecordsApiTest
{
    private AccountRenameStore accountRenameStore;
    private Account existingAccount;

    @BeforeMethod
    public void setUp()
    {
        super.setUp();

        accountRenameStore = new AccountRenameStore(accountRecordConverter, records, context);

        context.deleteFrom(ACCOUNT)
            .execute();

        AccountCreateStore accountCreateStore = new AccountCreateStore(accountRecordConverter, records);
        existingAccount = accountCreateStore.create(Account.builder()
            .email("someone@example.org")
            .displayName("Someone")
            .build());
    }

    @AfterMethod
    public void tearDown()
    {
        context.deleteFrom(ACCOUNT)
            .execute();

        super.tearDown();
    }

    @Test
    public void testRename()
    {
        assertThat(existingAccount.getRenameCount()).isEqualTo((short) 0);

        accountRenameStore.rename(existingAccount.getId(), "Mr. Johnson");

        Account expected = existingAccount.toBuilder()
            .displayName("Mr. Johnson")
            .renameCount((short) 1)
            .build();
        Account persisted = selectAccountDirectly(expected.getId());
        assertThat(persisted).usingRecursiveComparison()
            .ignoringFields(Account.Fields.timestamp)
            .isEqualTo(expected);
    }
}
