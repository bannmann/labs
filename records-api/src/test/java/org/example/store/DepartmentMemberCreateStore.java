package org.example.store;

import static org.example.Tables.DEPARTMENT_MEMBER;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import org.example.business.Department;
import org.example.business.DepartmentMember;

import com.github.bannmann.labs.records_api.Records;
import com.google.common.annotations.VisibleForTesting;

@VisibleForTesting
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DepartmentMemberCreateStore
{
    private final DepartmentMemberRecordConverter converter;
    private final Records records;

    public DepartmentMember create(DepartmentMember pojo)
    {
        return records.insertInto(DEPARTMENT_MEMBER)
            .withAnonymousConvertedVia(converter::fromPojo)
            .fromPojo(pojo)
            .executeAndConvertVia(converter::toPojo);
    }
}
