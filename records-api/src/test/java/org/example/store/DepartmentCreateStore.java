package org.example.store;

import static org.example.Tables.DEPARTMENT;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import org.example.business.Department;

import com.github.bannmann.labs.records_api.Records;
import com.google.common.annotations.VisibleForTesting;

@VisibleForTesting
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DepartmentCreateStore
{
    private final DepartmentRecordConverter converter;
    private final Records records;

    public Department create(Department pojo)
    {
        return records.insertInto(DEPARTMENT)
            .withIdentifiableConvertedVia(converter::fromPojo)
            .fromPojo(pojo)
            .generating(DEPARTMENT.TIMESTAMP)
            .executeAndConvert(converter::toPojo);
    }
}
