package dev.bannmann.labs.watchdog;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import lombok.extern.slf4j.Slf4j;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.codehaus.plexus.MutablePlexusContainer;

@Named
@Singleton
@Slf4j
final class WatchdogStartup extends AbstractMavenLifecycleParticipant
{
    private final LogTracker logTracker;

    @Inject
    public WatchdogStartup(MutablePlexusContainer mutablePlexusContainer, LogTracker logTracker)
    {
        this.logTracker = logTracker;

        try
        {
            replaceLoggerManager(mutablePlexusContainer);

            log.debug("Watchdog initialized.");
        }
        catch (Exception e)
        {
            if (log.isDebugEnabled())
            {
                log.warn("Watchdog initialization failed");
                log.debug("Failed to initialize watchdog", e);
            }
            else
            {
                log.warn("Watchdog initialization failed, rerun with -X to see exception");
            }
        }
    }

    private void replaceLoggerManager(MutablePlexusContainer mutablePlexusContainer)
    {
        var originalLoggerManager = mutablePlexusContainer.getLoggerManager();
        var loggerManager = new CustomLoggerManager(originalLoggerManager, logTracker);
        mutablePlexusContainer.setLoggerManager(loggerManager);
    }
}
