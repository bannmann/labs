package com.github.bannmann.labs.kumuluzee.errorhandling.jackson.jaxrs;

import javax.ws.rs.Priorities;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.ExceptionMapper;

import lombok.extern.slf4j.Slf4j;

import org.kohsuke.MetaInfServices;

import com.github.bannmann.labs.annotations.UpstreamCandidate;
import com.github.bannmann.labs.core.ClassExtras;
import com.github.bannmann.labs.kumuluzee.errorhandling.jaxrs.ErrorHandlingInitializer;

@MetaInfServices
@UpstreamCandidate("Mizool")
@Slf4j
public class JacksonErrorHandlingInitializer implements ErrorHandlingInitializer
{
    @Override
    public void initialize(FeatureContext context)
    {
        log.info("Initializing");

        registerIfExisting(context,
            "com.fasterxml.jackson.databind.JsonMappingException",
            MizoolJsonMappingExceptionMapper.class);

        registerIfExisting(context, "javax.validation.ValidationException", MizoolValidationExceptionMapper.class);
    }

    /**
     * Unfortunately, libraries like Jersey and Jackson include and/or register exception mappers which have more
     * specific type parameters than RestExceptionMapper. This means they always win, regardless of {@link
     * javax.annotation.Priority} values. So we need to register additional exception mappers for the same classes *and*
     * choose a better priority (= lower number) than the default ({@link Priorities#USER} = {@code 5000}.).
     */
    private void registerIfExisting(
        FeatureContext context, String className, Class<? extends ExceptionMapper<?>> mapperClass)
    {
        if (ClassExtras.tryResolve(className)
            .isPresent())
        {
            /*
             * Note: Originally, we used the register() overload with a priority value. However, it turned out that
             * Jersey's ExceptionMapperFactory#createLazyExceptionMappers() does not care about that value and only
             * uses the @Priority annotation value.
             */
            context.register(mapperClass);
        }
    }
}
