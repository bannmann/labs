package org.example;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.example.Tables.ACCOUNT;
import static org.example.Tables.DEPARTMENT;
import static org.example.Tables.DEPARTMENT_MEMBER;

import java.util.List;
import java.util.stream.Stream;

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

    private Account account;
    private Department astrometry;
    private Department engineering;

    @BeforeMethod
    public void setUp()
    {
        super.setUp();

        account = new AccountCreateStore(accountRecordConverter, records).create(Account.builder()
            .email("someone@example.org")
            .displayName("Someone")
            .ssoId(null)
            .build());

        var departmentCreateStore = new DepartmentCreateStore(new DepartmentRecordConverter(identifierConverter),
            records);
        astrometry = departmentCreateStore.create(Department.builder()
            .name("Astrometry")
            .build());
        engineering = departmentCreateStore.create(Department.builder()
            .name("Engineering")
            .build());

        departmentMemberCreateStore = new DepartmentMemberCreateStore(departmentMemberRecordConverter, records);
    }

    @AfterMethod
    public void tearDown()
    {
        context.deleteFrom(DEPARTMENT_MEMBER)
            .execute();

        context.deleteFrom(ACCOUNT)
            .where(ACCOUNT.ID.eq(account.getId()
                .getValue()))
            .execute();

        context.deleteFrom(DEPARTMENT)
            .where(DEPARTMENT.ID.in(astrometry.getId()
                    .getValue(),
                engineering.getId()
                    .getValue()))
            .execute();
    }

    @Test
    public void testCreateSingle()
    {
        DepartmentMember pojo = createMembershipPojo(astrometry, account);

        departmentMemberCreateStore.create(pojo);
    }

    private DepartmentMember createMembershipPojo(Department department, Account account)
    {
        return DepartmentMember.builder()
            .departmentId(department.getId())
            .accountId(account.getId())
            .build();
    }

    @Test
    public void testCreateMultiple()
    {
        List<DepartmentMember> pojos = Stream.of(astrometry, engineering)
            .map(department -> createMembershipPojo(department, account))
            .toList();

        departmentMemberCreateStore.create(pojos);
    }

    @Test
    public void testPrimaryKeyViolation()
    {
        DepartmentMember pojo = createMembershipPojo(astrometry, account);

        // First attempt works
        assertThatCode(() -> departmentMemberCreateStore.create(pojo)).doesNotThrowAnyException();

        // Second attempt fails
        assertThatThrownBy(() -> departmentMemberCreateStore.create(pojo)).isInstanceOf(ConflictingEntityException.class)
            .hasMessage("Conflict with existing entity due to DEPARTMENT_ID+ACCOUNT_ID");
    }
}
