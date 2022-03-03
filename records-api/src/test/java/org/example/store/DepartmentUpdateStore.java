package org.example.store;

import static org.example.Tables.DEPARTMENT;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import org.example.business.Department;

import com.github.bannmann.labs.records_api.Records;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DepartmentUpdateStore
{
    private final DepartmentRecordConverter converter;
    private final Records records;

    public Department update(Department pojo)
    {
        return records.update(DEPARTMENT)
            .withRecordConvertedVia(converter::fromPojo)
            .fromNewPojo(pojo)
            .checkAndRefresh(DEPARTMENT.TIMESTAMP)
            .executeAndConvert(converter::toPojo);
    }
}
