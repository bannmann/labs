package org.example.store;

import static org.example.Tables.DEPARTMENT;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import org.example.business.Department;
import org.example.tables.records.DepartmentRecord;

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
            .withIdentifiableConvertedVia(this::fromPojoCustom)
            .fromPojo(pojo)
            .generating(DEPARTMENT.TIMESTAMP)
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
