package org.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.example.Tables.ACCOUNT;
import static org.example.Tables.DEPARTMENT;

import java.util.Optional;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.example.business.Account;
import org.example.business.Department;
import org.example.store.AccountCreateStore;
import org.example.store.DepartmentCreateStore;
import org.example.store.DepartmentDeleteStore;
import org.example.store.DepartmentRecordConverter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.mizool.core.Identifier;
import com.github.mizool.core.exception.ObjectNotFoundException;
import com.github.mizool.core.exception.PermissionDeniedException;

@Test(dependsOnGroups = "DepartmentCreation")
public class TestDepartmentDeletion extends AbstractRecordsApiTest
{
    private static final Identifier<Department> FAKE_DEPARTMENT_ID = Identifier.forPojo(Department.class)
        .of("FAKE123");

    private final DepartmentRecordConverter departmentRecordConverter = new DepartmentRecordConverter(
        identifierConverter);

    private DepartmentDeleteStore departmentDeleteStore;
    private Department department;
    private Account jill;
    private Account barbara;

    @BeforeMethod
    public void setUp()
    {
        super.setUp();

        AccountCreateStore accountCreateStore = new AccountCreateStore(accountRecordConverter, records);
        jill = accountCreateStore.create(Account.builder()
            .email("jill@example.org")
            .displayName("Jill Underwood")
            .ssoId(null)
            .build());
        barbara = accountCreateStore.create(Account.builder()
            .email("barb@example.org")
            .displayName("Barbara 'Barb' Smith")
            .ssoId(null)
            .build());

        DepartmentCreateStore departmentCreateStore = new DepartmentCreateStore(departmentRecordConverter, records);
        department = departmentCreateStore.create(Department.builder()
            .name("Research & Development")
            .ownerAccountId(jill.getId())
            .build());

        departmentDeleteStore = new DepartmentDeleteStore(departmentRecordConverter, records);
    }

    @AfterMethod
    public void tearDown()
    {
        context.deleteFrom(DEPARTMENT)
            .where(DEPARTMENT.ID.eq(department.getId()
                .getValue()))
            .execute();

        context.deleteFrom(ACCOUNT)
            .where(ACCOUNT.ID.in(jill.getId()
                    .getValue(),
                barbara.getId()
                    .getValue()))
            .execute();
    }

    @Test
    public void testDelete()
    {
        Department deletedDepartment = departmentDeleteStore.delete(department.getId(), jill.getId());

        assertSoftly(softly -> {
            softly.assertThat(deletedDepartment)
                .describedAs("return value")
                .isEqualTo(department);

            Optional<Department> departmentFromDatabase = selectDepartmentDirectly(department.getId());
            softly.assertThat(departmentFromDatabase)
                .describedAs("database row")
                .isEmpty();
        });
    }

    private Optional<Department> selectDepartmentDirectly(Identifier<Department> id)
    {
        return context.selectFrom(DEPARTMENT)
            .where(DEPARTMENT.ID.eq(id.getValue()))
            .fetchOptional(departmentRecordConverter::toPojo);
    }

    @Test
    public void testDeleteDeniedForNonOwner()
    {
        ThrowingCallable deleteCall = () -> departmentDeleteStore.delete(department.getId(), barbara.getId());

        assertThatThrownBy(deleteCall).isInstanceOf(PermissionDeniedException.class);
    }

    @Test
    public void testDeleteNonExisting()
    {
        ThrowingCallable deleteCall = () -> departmentDeleteStore.delete(FAKE_DEPARTMENT_ID, jill.getId());

        assertThatThrownBy(deleteCall).isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    public void testTryDelete()
    {
        boolean result = departmentDeleteStore.tryDelete(department.getId(), jill.getId());

        assertSoftly(softly -> {
            softly.assertThat(result)
                .describedAs("return value")
                .isTrue();

            Optional<Department> departmentFromDatabase = selectDepartmentDirectly(department.getId());
            softly.assertThat(departmentFromDatabase)
                .describedAs("database row")
                .isEmpty();
        });
    }

    @Test
    public void testTryDeleteNonExisting()
    {
        boolean result = departmentDeleteStore.tryDelete(FAKE_DEPARTMENT_ID, jill.getId());

        assertThat(result).describedAs("return value")
            .isFalse();
    }

    @Test
    public void testTryDeleteAndGet()
    {
        Optional<Department> deletedDepartment = departmentDeleteStore.tryDeleteAndGet(department.getId(),
            jill.getId());

        assertSoftly(softly -> {
            softly.assertThat(deletedDepartment)
                .describedAs("return value")
                .contains(department);

            Optional<Department> departmentFromDatabase = selectDepartmentDirectly(department.getId());
            softly.assertThat(departmentFromDatabase)
                .describedAs("database row")
                .isEmpty();
        });
    }

    @Test
    public void testTryDeleteAndGetNonExisting()
    {
        Optional<Department> result = departmentDeleteStore.tryDeleteAndGet(FAKE_DEPARTMENT_ID, jill.getId());

        assertThat(result).describedAs("return value")
            .isEmpty();
    }
}
