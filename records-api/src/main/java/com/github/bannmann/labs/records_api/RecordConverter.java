package com.github.bannmann.labs.records_api;

import org.jooq.Record;

public interface RecordConverter<P, R extends Record>
{
    R fromPojo(P pojo);

    P toPojo(R record);
}
