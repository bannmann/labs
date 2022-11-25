package org.example;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.example.store.DepartmentMemberUpdateStore;
import org.example.store.DepartmentRecordConverter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.mizool.core.Identifier;

@Test(groups = "AccountCreation")
public class TestDepartmentMemberUpdating extends AbstractRecordsApiTest
{
    private final DepartmentMemberRecordConverter departmentMemberRecordConverter = new DepartmentMemberRecordConverter(
        identifierConverter);

    private DepartmentMemberUpdateStore departmentMemberUpdateStore;

    private Account existingAccount;
    private Department existingDepartment;
    private DepartmentMember existingMember;

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

        existingMember = new DepartmentMemberCreateStore(departmentMemberRecordConverter, records).create(
            DepartmentMember.builder()
                .departmentId(existingDepartment.getId())
                .accountId(existingAccount.getId())
                .build());

        departmentMemberUpdateStore = new DepartmentMemberUpdateStore(departmentMemberRecordConverter, records);
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
    public void testUpdate()
    {
        DepartmentMember pojo = existingMember.toBuilder()
            .permissionLevel(1)
            .build();

        DepartmentMember result = departmentMemberUpdateStore.update(pojo);

        assertPersistedEntityMatches(result);
    }

    private void assertPersistedEntityMatches(DepartmentMember expected)
    {
        DepartmentMember persisted = selectDepartmentMemberDirectly(expected.getDepartmentId(),
            expected.getAccountId());
        assertThat(persisted).usingRecursiveComparison()
            .isEqualTo(expected);
    }

    protected DepartmentMember selectDepartmentMemberDirectly(
        Identifier<Department> departmentId, Identifier<Account> accountId)
    {
        return context.selectFrom(DEPARTMENT_MEMBER)
            .where(DEPARTMENT_MEMBER.DEPARTMENT_ID.eq(departmentId.getValue()))
            .and(DEPARTMENT_MEMBER.ACCOUNT_ID.eq(accountId.getValue()))
            .fetchOne(departmentMemberRecordConverter::toPojo);
    }
}
