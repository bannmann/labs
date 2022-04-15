package com.github.bannmann.labs.kumuluzee.errorhandling.jaxrs;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.github.mizool.core.rest.errorhandling.RestExceptionMapper;

public abstract class AbstractMizoolExceptionMapper<T extends Exception> implements ExceptionMapper<T>
{
    @Inject
    private RestExceptionMapper restExceptionMapper;

    @Override
    public Response toResponse(T exception)
    {
        return restExceptionMapper.toResponse(exception);
    }
}
