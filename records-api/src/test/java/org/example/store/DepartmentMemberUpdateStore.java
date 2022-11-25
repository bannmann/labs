package org.example.store;

import static org.example.Tables.DEPARTMENT_MEMBER;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import org.example.business.DepartmentMember;

import com.github.bannmann.labs.records_api.Records;
import com.google.common.annotations.VisibleForTesting;

@VisibleForTesting
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DepartmentMemberUpdateStore
{
    private final DepartmentMemberRecordConverter converter;
    private final Records records;

    public DepartmentMember update(DepartmentMember pojo)
    {
        return records.update(DEPARTMENT_MEMBER)
            .withRecordConvertedUsing(converter)
            .fromNewPojo(pojo)
            .executeAndConvert();
    }
}
