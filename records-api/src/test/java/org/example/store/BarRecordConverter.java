package org.example.store;

import org.example.business.Bar;
import org.example.tables.records.BarRecord;

import com.github.mizool.core.exception.CodeInconsistencyException;
import com.google.common.annotations.VisibleForTesting;
import dev.bannmann.labs.records_api.RecordConverter;

@VisibleForTesting
public class BarRecordConverter implements RecordConverter<Bar, BarRecord>
{
    public BarRecord fromPojo(Bar pojo)
    {
        throw new CodeInconsistencyException("intended for compile time only");
    }

    public Bar toPojo(BarRecord record)
    {
        throw new CodeInconsistencyException("intended for compile time only");
    }
}
