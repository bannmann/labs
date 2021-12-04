package com.github.bannmann.labs.records_api;

import com.github.mizool.core.exception.AbstractUnprocessableEntityException;

/**
 * Thrown when an attempt is made to set a value for a field which is automatically generated or maintained, such as
 * the identifier or timestamps.
 */
public class GeneratedFieldOverrideException extends AbstractUnprocessableEntityException
{
    // TODO use mizool version instead: https://github.com/mizool/mizool/pull/203
    public GeneratedFieldOverrideException()
    {
        super();
    }

    public GeneratedFieldOverrideException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public GeneratedFieldOverrideException(String message)
    {
        super(message);
    }

    public GeneratedFieldOverrideException(Throwable cause)
    {
        super(cause);
    }
}
