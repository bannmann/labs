package com.github.bannmann.labs.kumuluzee.errorhandling;

import java.lang.reflect.Field;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.eclipse.jetty.webapp.WebAppContext;
import org.kohsuke.MetaInfServices;

import com.github.bannmann.labs.kumuluzee.errorhandling.jetty.JettyWebAppContextInitializer;
import com.github.mizool.core.exception.CodeInconsistencyException;
import com.kumuluz.ee.common.Extension;
import com.kumuluz.ee.common.KumuluzServer;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.dependencies.EeExtensionDef;
import com.kumuluz.ee.common.wrapper.KumuluzServerWrapper;

@EeExtensionDef(group = "mizool", name = "ErrorHandling")
@MetaInfServices
@Slf4j
public class ErrorHandlingExtension implements Extension
{
    @Override
    public void load()
    {
    }

    @Override
    public void init(KumuluzServerWrapper server, EeConfig eeConfig)
    {
        WebAppContext webAppContext = obtainWebAppContext(server);
        JettyWebAppContextInitializer.runAll(webAppContext);
    }

    private WebAppContext obtainWebAppContext(KumuluzServerWrapper server)
    {
        // TODO get Kumuluz to offer a proper API for this and use it instead of this ugly hack
        // Approach copied from https://github.com/kumuluz/kumuluzee/issues/132
        try
        {
            KumuluzServer kumuluzServer = server.getServer();
            Field field = kumuluzServer.getClass()
                .getDeclaredField("appContext");
            field.setAccessible(true);
            return (WebAppContext) field.get(kumuluzServer);
        }
        catch (ReflectiveOperationException e)
        {
            throw new CodeInconsistencyException(e);
        }
    }

    @Override
    public List<String> scanLibraries()
    {
        String fullPath = getClass().getProtectionDomain()
            .getCodeSource()
            .getLocation()
            .getPath();
        log.debug("full path: {}", fullPath);

        String fileName = fullPath.substring(fullPath.lastIndexOf("/") + 1);
        log.debug("jar name: {}", fileName);

        return List.of(fileName);
    }
}
