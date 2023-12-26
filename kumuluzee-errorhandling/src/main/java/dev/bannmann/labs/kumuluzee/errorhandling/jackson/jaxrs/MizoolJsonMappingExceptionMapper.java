package dev.bannmann.labs.kumuluzee.errorhandling.jackson.jaxrs;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;

import com.fasterxml.jackson.databind.JsonMappingException;
import dev.bannmann.labs.kumuluzee.errorhandling.jaxrs.AbstractMizoolExceptionMapper;

@Priority(Priorities.ENTITY_CODER + 761)
public class MizoolJsonMappingExceptionMapper extends AbstractMizoolExceptionMapper<JsonMappingException>
{
}
