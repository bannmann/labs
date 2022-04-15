package com.github.bannmann.labs.kumuluzee.errorhandling.jaxrs;

import javax.ws.rs.core.FeatureContext;

public interface ErrorHandlingInitializer
{
    void initialize(FeatureContext context);
}
