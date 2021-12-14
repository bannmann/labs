package org.example;

import static org.assertj.core.api.Assertions.assertThat;

import org.example.business.Account;
import org.example.store.AccountCreateStore;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestAccountCreation extends AbstractRecordsApiTest
{
    private AccountCreateStore accountCreateStore;

    @BeforeClass
    public void setUp()
    {
        super.setUp();
        accountCreateStore = new AccountCreateStore(accountRecordConverter, records);
    }

    @Test
    public void test()
    {
        Account pojo = Account.builder()
            .email("John.Doe@EXAMPLE.ORG")
            .build();

        Account result = accountCreateStore.create(pojo);

        assertThat(result).hasNoNullFieldsOrProperties();

        Account persisted = readFromDatabase(result.getId());
        assertThat(persisted).isEqualTo(result);
    }
}
