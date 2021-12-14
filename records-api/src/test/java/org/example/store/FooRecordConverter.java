package org.example.store;

import org.example.business.Foo;
import org.example.tables.records.FooRecord;

import com.github.mizool.core.exception.CodeInconsistencyException;
import com.google.common.annotations.VisibleForTesting;

@VisibleForTesting
public class FooRecordConverter
{
    public FooRecord fromPojo(Foo pojo)
    {
        throw new CodeInconsistencyException("intended for compile time only");
    }

    public Foo toPojo(FooRecord record)
    {
        throw new CodeInconsistencyException("intended for compile time only");
    }
}
