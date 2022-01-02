package com.github.bannmann.labs.records_api;

/**
 * Wraps a string suitable for log/exception messages containing table name and primary key of a record.
 */
final class RecordKey extends StringWrapper
{
    public RecordKey(String contents)
    {
        super(contents);
    }
}
