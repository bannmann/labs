package com.github.bannmann.labs.kumuluzee.errorhandling.jetty;

import org.eclipse.jetty.webapp.WebAppContext;
import org.kohsuke.MetaInfServices;

@MetaInfServices
public class JettyErrorHandlerInitializer implements JettyWebAppContextInitializer
{
    @Override
    public void initialize(WebAppContext webAppContext)
    {
        webAppContext.setErrorHandler(new JettyErrorHandler());
    }
}
