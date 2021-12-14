package org.example.store;

import org.example.business.Thud;
import org.example.tables.records.ThudRecord;

import com.github.mizool.core.exception.CodeInconsistencyException;
import com.google.common.annotations.VisibleForTesting;

@VisibleForTesting
public class ThudRecordConverter
{
    public ThudRecord fromPojo(Thud pojo)
    {
        throw new CodeInconsistencyException("intended for compile time only");
    }

    public Thud toPojo(ThudRecord record)
    {
        throw new CodeInconsistencyException("intended for compile time only");
    }
}
