package com.github.bannmann.labs.kumuluzee.errorhandling.jetty;

import org.eclipse.jetty.webapp.WebAppContext;

import com.github.mizool.core.MetaInfServices;

public interface JettyWebAppContextInitializer
{
    static void runAll(WebAppContext webAppContext)
    {
        for (var initializer : MetaInfServices.instances(JettyWebAppContextInitializer.class))
        {
            initializer.initialize(webAppContext);
        }
    }

    void initialize(WebAppContext webAppContext);
}
