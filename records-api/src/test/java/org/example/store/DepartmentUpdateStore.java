package org.example.store;

import static org.example.Tables.DEPARTMENT;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import org.example.business.Department;
import org.example.tables.records.DepartmentRecord;

import com.github.bannmann.labs.records_api.Records;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DepartmentUpdateStore
{
    private final DepartmentRecordConverter converter;
    private final Records records;

    public Department update(Department pojo)
    {
        return records.update(DEPARTMENT)
            .withRecordConvertedVia(this::fromPojoCustom)
            .fromNewPojo(pojo)
            .checkAndRefresh(DEPARTMENT.TIMESTAMP)
            .executeAndConvertVia(this::toPojoCustom);
    }

    /**
     * Simulate the use case where one doesn't have a {@link com.github.bannmann.labs.records_api.RecordConverter} and
     * therefore has to pass individual lambdas.
     */
    private DepartmentRecord fromPojoCustom(Department pojo)
    {
        return converter.fromPojo(pojo);
    }

    /**
     * Simulate the use case where one doesn't have a {@link com.github.bannmann.labs.records_api.RecordConverter} and
     * therefore has to pass individual lambdas.
     */
    private Department toPojoCustom(DepartmentRecord record)
    {
        return converter.toPojo(record);
    }
}
