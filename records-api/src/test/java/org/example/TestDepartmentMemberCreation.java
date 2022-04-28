package org.example;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.example.Tables.ACCOUNT;
import static org.example.Tables.DEPARTMENT;
import static org.example.Tables.DEPARTMENT_MEMBER;

import org.example.business.Account;
import org.example.business.Department;
import org.example.business.DepartmentMember;
import org.example.store.AccountCreateStore;
import org.example.store.DepartmentCreateStore;
import org.example.store.DepartmentMemberCreateStore;
import org.example.store.DepartmentMemberRecordConverter;
import org.example.store.DepartmentRecordConverter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.mizool.core.exception.ConflictingEntityException;

@Test(groups = "AccountCreation")
public class TestDepartmentMemberCreation extends AbstractRecordsApiTest
{
    private final DepartmentMemberRecordConverter departmentMemberRecordConverter = new DepartmentMemberRecordConverter(
        identifierConverter);

    private DepartmentMemberCreateStore departmentMemberCreateStore;

    private Account existingAccount;
    private Department existingDepartment;

    @BeforeMethod
    public void setUp()
    {
        super.setUp();

        existingAccount = new AccountCreateStore(accountRecordConverter, records).create(Account.builder()
            .email("someone@example.org")
            .displayName("Someone")
            .ssoId(null)
            .build());

        existingDepartment = new DepartmentCreateStore(new DepartmentRecordConverter(identifierConverter),
            records).create(Department.builder()
            .name("Astrometry")
            .build());

        departmentMemberCreateStore = new DepartmentMemberCreateStore(departmentMemberRecordConverter, records);
    }

    @AfterMethod
    public void tearDown()
    {
        context.deleteFrom(DEPARTMENT_MEMBER)
            .execute();

        context.deleteFrom(ACCOUNT)
            .where(ACCOUNT.ID.eq(existingAccount.getId()
                .getValue()))
            .execute();

        context.deleteFrom(DEPARTMENT)
            .where(DEPARTMENT.ID.eq(existingDepartment.getId()
                .getValue()))
            .execute();

        super.tearDown();
    }

    @Test
    public void testCreate()
    {
        DepartmentMember pojo = DepartmentMember.builder()
            .departmentId(existingDepartment.getId())
            .accountId(existingAccount.getId())
            .build();

        departmentMemberCreateStore.create(pojo);
    }

    @Test
    public void testPrimaryKeyViolation()
    {
        DepartmentMember pojo = DepartmentMember.builder()
            .departmentId(existingDepartment.getId())
            .accountId(existingAccount.getId())
            .build();

        assertThatCode(() -> departmentMemberCreateStore.create(pojo)).doesNotThrowAnyException();
        assertThatThrownBy(() -> departmentMemberCreateStore.create(pojo)).isInstanceOf(ConflictingEntityException.class)
            .hasMessage("Conflict with existing entity due to DEPARTMENT_ID+ACCOUNT_ID");
    }
}
