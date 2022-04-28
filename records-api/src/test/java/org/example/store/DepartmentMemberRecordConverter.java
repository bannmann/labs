package org.example.store;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import org.example.business.Account;
import org.example.business.Department;
import org.example.business.DepartmentMember;
import org.example.tables.records.DepartmentMemberRecord;

import com.github.mizool.core.converter.IdentifierConverter;
import com.google.common.annotations.VisibleForTesting;

@VisibleForTesting
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DepartmentMemberRecordConverter
{
    private final IdentifierConverter identifierConverter;

    public DepartmentMemberRecord fromPojo(DepartmentMember pojo)
    {
        DepartmentMemberRecord result = null;

        if (pojo != null)
        {
            result = new DepartmentMemberRecord();
            result.setDepartmentId(identifierConverter.fromPojo(pojo.getDepartmentId()));
            result.setAccountId(identifierConverter.fromPojo(pojo.getAccountId()));
        }

        return result;
    }

    public DepartmentMember toPojo(DepartmentMemberRecord record)
    {
        DepartmentMember result = null;

        if (record != null)
        {
            result = DepartmentMember.builder()
                .departmentId(identifierConverter.toPojo(record.getDepartmentId(), Department.class))
                .accountId(identifierConverter.toPojo(record.getAccountId(), Account.class))
                .build();
        }
        return result;
    }
}
