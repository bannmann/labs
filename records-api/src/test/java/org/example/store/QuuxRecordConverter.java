package org.example.store;

import org.example.business.Quux;
import org.example.tables.records.QuuxRecord;

import com.github.mizool.core.exception.CodeInconsistencyException;
import com.google.common.annotations.VisibleForTesting;
import dev.bannmann.labs.records_api.RecordConverter;

@VisibleForTesting
public class QuuxRecordConverter implements RecordConverter<Quux, QuuxRecord>
{
    public QuuxRecord fromPojo(Quux pojo)
    {
        throw new CodeInconsistencyException("intended for compile time only");
    }

    public Quux toPojo(QuuxRecord record)
    {
        throw new CodeInconsistencyException("intended for compile time only");
    }
}
