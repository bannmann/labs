package org.example.store;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import org.example.business.Account;
import org.example.business.Department;
import org.example.tables.records.DepartmentRecord;

import com.github.mizool.core.converter.IdentifierConverter;
import com.google.common.annotations.VisibleForTesting;

@VisibleForTesting
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DepartmentRecordConverter
{
    private final IdentifierConverter identifierConverter;

    public DepartmentRecord fromPojo(Department pojo)
    {
        DepartmentRecord result = null;

        if (pojo != null)
        {
            result = new DepartmentRecord();
            result.setId(identifierConverter.fromPojo(pojo.getId()));
            result.setName(pojo.getName());
            result.setOwnerAccountId(identifierConverter.fromPojo(pojo.getOwnerAccountId()));
            result.setTimestamp(pojo.getTimestamp());
        }

        return result;
    }

    public Department toPojo(DepartmentRecord record)
    {
        Department result = null;

        if (record != null)
        {
            result = Department.builder()
                .id(identifierConverter.toPojo(record.getId(), Department.class))
                .name(record.getName())
                .ownerAccountId(identifierConverter.toPojo(record.getOwnerAccountId(), Account.class))
                .timestamp(record.getTimestamp())
                .build();
        }
        return result;
    }
}
