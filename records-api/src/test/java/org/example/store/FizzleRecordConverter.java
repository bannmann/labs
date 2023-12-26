package org.example.store;

import org.example.business.Fizzle;
import org.example.tables.records.FizzleRecord;

import com.github.mizool.core.exception.CodeInconsistencyException;
import com.google.common.annotations.VisibleForTesting;
import dev.bannmann.labs.records_api.RecordConverter;

@VisibleForTesting
public class FizzleRecordConverter implements RecordConverter<Fizzle, FizzleRecord>
{
    public FizzleRecord fromPojo(Fizzle pojo)
    {
        throw new CodeInconsistencyException("intended for compile time only");
    }

    public Fizzle toPojo(FizzleRecord record)
    {
        throw new CodeInconsistencyException("intended for compile time only");
    }
}
