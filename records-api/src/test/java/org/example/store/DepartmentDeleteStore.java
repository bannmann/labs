package org.example.store;

import static org.example.Tables.DEPARTMENT;

import java.util.Optional;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import org.example.business.Account;
import org.example.business.Department;

import com.github.mizool.core.Identifier;
import dev.bannmann.labs.records_api.Records;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DepartmentDeleteStore
{
    private final DepartmentRecordConverter converter;
    private final Records records;

    public Department delete(Identifier<Department> id, Identifier<Account> loggedInAccount)
    {
        return records.delete()
            .fromIdentifiable(DEPARTMENT, converter)
            .withId(id)
            .denyUnless(DEPARTMENT.OWNER_ACCOUNT_ID.eq(loggedInAccount.getValue()))
            .executeAndConvert();
    }

    public boolean tryDelete(Identifier<Department> id, Identifier<Account> loggedInAccount)
    {
        return records.delete()
            .fromIdentifiable(DEPARTMENT, converter)
            .withId(id)
            .denyUnless(DEPARTMENT.OWNER_ACCOUNT_ID.eq(loggedInAccount.getValue()))
            .ignoreIfNotFound()
            .tryExecute();
    }

    public Optional<Department> tryDeleteAndGet(Identifier<Department> id, Identifier<Account> loggedInAccount)
    {
        return records.delete()
            .fromIdentifiable(DEPARTMENT, converter)
            .withId(id)
            .denyUnless(DEPARTMENT.OWNER_ACCOUNT_ID.eq(loggedInAccount.getValue()))
            .ignoreIfNotFound()
            .tryExecuteAndConvert();
    }
}
