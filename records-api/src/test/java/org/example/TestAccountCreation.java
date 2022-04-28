package org.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.example.business.Account;
import org.example.store.AccountCreateStore;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.mizool.core.Identifier;
import com.github.mizool.core.exception.ConflictingEntityException;
import com.github.mizool.core.exception.GeneratedFieldOverrideException;

@Test(groups = "AccountCreation")
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
    public void testCreate()
    {
        Account pojo = Account.builder()
            .email("John.Doe@EXAMPLE.ORG")
            .displayName("J. Doe")
            .build();

        Account result = accountCreateStore.create(pojo);

        assertThat(result).hasNoNullFieldsOrPropertiesExcept(Account.Fields.ssoId)
            .returns("john.doe@example.org", Account::getEmail);

        Account persisted = selectDirectly(result.getId());
        assertThat(persisted).isEqualTo(result);
    }

    @Test
    public void testRejectsIfIdGiven()
    {
        Account pojo = Account.builder()
            .id(Identifier.forPojo(Account.class)
                .random())
            .email("john.doe@example.org")
            .displayName("J. Doe")
            .build();

        assertThatThrownBy(() -> accountCreateStore.create(pojo)).isInstanceOf(GeneratedFieldOverrideException.class);
    }

    @Test
    public void testUniqueConstraintViolation()
    {
        Account firstAccount = Account.builder()
            .email("smith@example.org")
            .displayName("Jane Smith")
            .build();
        accountCreateStore.create(firstAccount);

        Account secondAccount = Account.builder()
            .email("smith@example.org")
            .displayName("John Smith")
            .build();
        assertThatThrownBy(() -> accountCreateStore.create(secondAccount)).isInstanceOf(ConflictingEntityException.class)
            .hasMessage("Conflict with existing entity due to EMAIL");
    }
}
