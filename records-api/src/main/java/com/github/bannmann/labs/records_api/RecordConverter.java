package com.github.bannmann.labs.records_api;

import org.jooq.UpdatableRecord;

public interface RecordConverter<P, R extends UpdatableRecord<R>>
{
    R fromPojo(P pojo);

    P toPojo(R record);
}
