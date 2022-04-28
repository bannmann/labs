package org.example;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.example.Tables.ACCOUNT;
import static org.example.Tables.DEPARTMENT;

import org.example.business.Account;
import org.example.business.Department;
import org.example.store.AccountCreateStore;
import org.example.store.DepartmentCreateStore;
import org.example.store.DepartmentRecordConverter;
import org.example.store.DepartmentUpdateStore;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.bannmann.labs.records_api.EntityReferenceException;
import com.github.mizool.core.Identifier;

@Test(dependsOnGroups = "DepartmentCreation")
public class TestDepartmentUpdating extends AbstractRecordsApiTest
{
    private final DepartmentRecordConverter departmentRecordConverter = new DepartmentRecordConverter(
        identifierConverter);

    private DepartmentUpdateStore departmentUpdateStore;
    private Department existingDepartment;
    private Account existingAccount;

    @BeforeClass
    public void setUp()
    {
        super.setUp();

        AccountCreateStore accountCreateStore = new AccountCreateStore(accountRecordConverter, records);
        existingAccount = accountCreateStore.create(Account.builder()
            .email("someone@example.org")
            .displayName("Someone")
            .ssoId(null)
            .build());

        DepartmentCreateStore departmentCreateStore = new DepartmentCreateStore(departmentRecordConverter, records);
        existingDepartment = departmentCreateStore.create(Department.builder()
            .name("Research & Development")
            .ownerAccountId(existingAccount.getId())
            .build());

        departmentUpdateStore = new DepartmentUpdateStore(departmentRecordConverter, records);
    }

    @AfterMethod
    public void tearDown()
    {
        context.deleteFrom(DEPARTMENT)
            .where(DEPARTMENT.ID.eq(existingDepartment.getId()
                .getValue()))
            .execute();

        context.deleteFrom(ACCOUNT)
            .where(ACCOUNT.ID.eq(existingAccount.getId()
                .getValue()))
            .execute();

        super.tearDown();
    }

    @Test
    public void testForeignKeyConstraintViolation()
    {
        Department newPojo = existingDepartment.toBuilder()
            .ownerAccountId(Identifier.forPojo(Account.class)
                .of("ABC123NOPE"))
            .build();

        assertThatThrownBy(() -> departmentUpdateStore.update(newPojo)).isInstanceOf(EntityReferenceException.class)
            .hasMessage("Invalid entity reference in field OWNER_ACCOUNT_ID");
    }
}
