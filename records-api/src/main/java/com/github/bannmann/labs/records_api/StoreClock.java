package com.github.bannmann.labs.records_api;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

/**
 * TODO decide if/how to replace StoreClock with regular 'Clock' without sacrificing Charlie hook for truncating
 */
public class StoreClock
{
    private static final Clock clock = Clock.systemUTC();

    /**
     * @return current time, truncated to milliseconds
     */
    public OffsetDateTime now()
    {
        return OffsetDateTime.now(clock)
            .truncatedTo(ChronoUnit.MILLIS);
    }
}
