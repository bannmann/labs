package org.example.store;

import org.example.business.Corge;
import org.example.tables.records.CorgeRecord;

import com.github.mizool.core.exception.CodeInconsistencyException;
import com.google.common.annotations.VisibleForTesting;
import dev.bannmann.labs.records_api.RecordConverter;

@VisibleForTesting
public class CorgeRecordConverter implements RecordConverter<Corge, CorgeRecord>
{
    public CorgeRecord fromPojo(Corge pojo)
    {
        throw new CodeInconsistencyException("intended for compile time only");
    }

    public Corge toPojo(CorgeRecord record)
    {
        throw new CodeInconsistencyException("intended for compile time only");
    }
}
