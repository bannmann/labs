package dev.bannmann.labs.kumuluzee.errorhandling.jersey;

import javax.ws.rs.core.FeatureContext;

import lombok.extern.slf4j.Slf4j;

import org.glassfish.jersey.internal.spi.AutoDiscoverable;
import org.kohsuke.MetaInfServices;

import dev.bannmann.labs.annotations.UpstreamCandidate;
import dev.bannmann.labs.kumuluzee.errorhandling.jaxrs.ErrorHandlingFeature;

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
