package com.github.bannmann.labs.records_api;

/**
 * Wraps the field name (if given) or the user-provided name of a check.
 */
final class CheckLabel extends StringWrapper
{
    public CheckLabel(String contents)
    {
        super(contents);
    }
}
