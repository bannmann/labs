package com.github.bannmann.labs.kumuluzee.errorhandling.jaxrs;

import javax.ws.rs.Priorities;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import lombok.extern.slf4j.Slf4j;

import com.github.bannmann.labs.annotations.UpstreamCandidate;
import com.github.mizool.core.MetaInfServices;
import com.github.mizool.core.rest.errorhandling.RestExceptionMapper;

@UpstreamCandidate("Mizool")
@Slf4j
public class ErrorHandlingFeature implements Feature
{
    public ErrorHandlingFeature()
    {
        log.info("Instantiated.");
    }

    @Override
    public boolean configure(FeatureContext context)
    {
        log.info("Configuring {}", context);

        registerDefaultMapper(context);
        invokeInitializers(context);

        return true;
    }

    /**
     * As the {@link RestExceptionMapper} class doesn't have a {@link javax.annotation.Priority} annotation yet, we
     * register the mapper explicitly here and set an appropriate priority (smaller numbers come first; default is
     * {@link Priorities#USER} = {@code 5000}.).
     */
    private void registerDefaultMapper(FeatureContext context)
    {
        context.register(RestExceptionMapper.class, Priorities.ENTITY_CODER);
    }

    private void invokeInitializers(FeatureContext context)
    {
        for (var initializer : MetaInfServices.instances(ErrorHandlingInitializer.class))
        {
            initializer.initialize(context);
        }
    }
}
