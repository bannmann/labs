package org.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.Tables.ACCOUNT;

import java.util.Optional;

import org.example.business.Account;
import org.example.store.AccountCreateStore;
import org.example.store.AccountFindByEmailStore;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class TestAccountFinder extends AbstractRecordsApiTest
{
    private AccountFindByEmailStore accountFindByEmailStore;
    private Account existingAccount;

    @BeforeMethod
    public void setUp()
    {
        super.setUp();

        accountFindByEmailStore = new AccountFindByEmailStore(records, accountRecordConverter);

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
    public void testFind()
    {
        Optional<Account> actual = accountFindByEmailStore.find("someone@example.org");

        assertThat(actual).contains(existingAccount);
    }
}
