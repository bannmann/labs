package com.github.bannmann.labs.records_api;

import java.util.function.Function;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
class Violation
{
    CheckReason checkReason;

    /**
     * The field name (if given) or the user-provided name of the violated check
     */
    CheckLabel checkLabel;

    Function<RecordKey, RuntimeException> getExceptionBuilder()
    {
        return recordKey -> checkReason.getExceptionBuilder()
            .apply(recordKey, checkLabel);
    }
}
