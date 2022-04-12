package com.github.bannmann.labs.kumuluzee.errorhandling.jersey;

import javax.ws.rs.core.FeatureContext;

import lombok.extern.slf4j.Slf4j;

import org.glassfish.jersey.internal.spi.AutoDiscoverable;
import org.kohsuke.MetaInfServices;

import com.github.bannmann.labs.annotations.UpstreamCandidate;
import com.github.bannmann.labs.kumuluzee.errorhandling.jaxrs.ErrorHandlingFeature;

@UpstreamCandidate("Mizool")
@Slf4j
@MetaInfServices
public class AutoDiscoverableErrorHandlingFeature implements AutoDiscoverable
{
    @Override
    public void configure(FeatureContext context)
    {
        new ErrorHandlingFeature().configure(context);
    }
}
