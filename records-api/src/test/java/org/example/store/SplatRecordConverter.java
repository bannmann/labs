package org.example.store;

import org.example.business.Splat;
import org.example.tables.records.SplatRecord;

import com.github.mizool.core.exception.CodeInconsistencyException;
import com.google.common.annotations.VisibleForTesting;
import dev.bannmann.labs.records_api.RecordConverter;

@VisibleForTesting
public class SplatRecordConverter implements RecordConverter<Splat, SplatRecord>
{
    public SplatRecord fromPojo(Splat pojo)
    {
        throw new CodeInconsistencyException("intended for compile time only");
    }

    public Splat toPojo(SplatRecord record)
    {
        throw new CodeInconsistencyException("intended for compile time only");
    }
}
