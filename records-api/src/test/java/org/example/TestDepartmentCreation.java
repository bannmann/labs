package org.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.example.Tables.ACCOUNT;
import static org.example.Tables.DEPARTMENT;

import org.example.business.Account;
import org.example.business.Department;
import org.example.store.AccountCreateStore;
import org.example.store.DepartmentCreateStore;
import org.example.store.DepartmentRecordConverter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.bannmann.labs.records_api.EntityReferenceException;
import com.github.mizool.core.Identifier;
import com.github.mizool.core.exception.StoreLayerException;

@Test(groups = "DepartmentCreation")
public class TestDepartmentCreation extends AbstractRecordsApiTest
{
    private final DepartmentRecordConverter departmentRecordConverter = new DepartmentRecordConverter(
        identifierConverter);

    private DepartmentCreateStore departmentCreateStore;
    private Account existingAccount;

    @BeforeMethod
    public void setUp()
    {
        super.setUp();

        AccountCreateStore accountCreateStore = new AccountCreateStore(accountRecordConverter, records);
        existingAccount = accountCreateStore.create(Account.builder()
            .email("someone@example.org")
            .displayName("Someone")
            .ssoId(null)
            .build());

        departmentCreateStore = new DepartmentCreateStore(departmentRecordConverter, records);
    }

    @AfterMethod
    public void tearDown()
    {
        String existingAccountIdString = existingAccount.getId()
            .getValue();

        context.deleteFrom(DEPARTMENT)
            .where(DEPARTMENT.OWNER_ACCOUNT_ID.eq(existingAccountIdString))
            .execute();

        context.deleteFrom(ACCOUNT)
            .where(ACCOUNT.ID.eq(existingAccountIdString))
            .execute();

        super.tearDown();
    }

    @Test
    public void testCreate()
    {
        Department pojo = Department.builder()
            .name("Research & Development")
            .ownerAccountId(existingAccount.getId())
            .build();

        Department result = departmentCreateStore.create(pojo);

        assertThat(result).hasNoNullFieldsOrProperties();
    }

    /**
     * If a "NOT NULL" constraint in the table is not covered by a @NonNull in the Pojo, we consider that to be a
     * mistake by the developer. As such, it's not worth writing detection code specific to this scenario. On the other
     * hand, we should never throw a {@link com.github.mizool.core.exception.ConflictingEntityException} because that
     * would be misleading. So it is important to ensure this scenario results in a generic {@link
     * com.github.mizool.core.exception.StoreLayerException}.
     */
    @Test
    public void testNullConstraintViolationNotConfusedWithConflictingEntity()
    {
        Department pojo = Department.builder()
            .name(null)
            .ownerAccountId(existingAccount.getId())
            .build();
        assertThatThrownBy(() -> departmentCreateStore.create(pojo)).isInstanceOf(StoreLayerException.class);
    }

    @Test
    public void testForeignKeyConstraintViolation()
    {
        Department pojo = Department.builder()
            .name("Research & Development")
            .ownerAccountId(Identifier.forPojo(Account.class)
                .of("ABC123NOPE"))
            .build();

        assertThatThrownBy(() -> departmentCreateStore.create(pojo)).isInstanceOf(EntityReferenceException.class)
            .hasMessage("Invalid entity reference in field OWNER_ACCOUNT_ID");
    }
}
