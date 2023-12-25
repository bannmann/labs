package org.example.store;

import static org.example.Tables.DEPARTMENT_MEMBER;

import java.util.List;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import org.example.business.DepartmentMember;

import com.google.common.annotations.VisibleForTesting;
import dev.bannmann.labs.records_api.Records;

@VisibleForTesting
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DepartmentMemberCreateStore
{
    private final DepartmentMemberRecordConverter converter;
    private final Records records;

    public DepartmentMember create(DepartmentMember pojo)
    {
        return records.insertInto(DEPARTMENT_MEMBER)
            .withCustomKeyedConvertedUsing(converter)
            .fromPojo(pojo)
            .executeAndConvert();
    }

    public void create(List<DepartmentMember> pojos)
    {
        records.insertInto(DEPARTMENT_MEMBER)
            .withCustomKeyedConvertedUsing(converter)
            .fromPojos(pojos)
            .voidExecute();
    }
}
