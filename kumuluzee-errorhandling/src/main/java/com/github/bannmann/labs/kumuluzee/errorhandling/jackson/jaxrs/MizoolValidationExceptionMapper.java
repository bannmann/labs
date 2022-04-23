package com.github.bannmann.labs.kumuluzee.errorhandling.jackson.jaxrs;

import javax.annotation.Priority;
import javax.validation.ValidationException;
import javax.ws.rs.Priorities;

import com.github.bannmann.labs.kumuluzee.errorhandling.jaxrs.AbstractMizoolExceptionMapper;

@Priority(Priorities.ENTITY_CODER + 475)
public class MizoolValidationExceptionMapper extends AbstractMizoolExceptionMapper<ValidationException>
{
}
