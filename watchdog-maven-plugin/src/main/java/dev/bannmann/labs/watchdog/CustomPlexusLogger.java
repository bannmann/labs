package dev.bannmann.labs.watchdog;

import lombok.extern.slf4j.Slf4j;

import org.codehaus.plexus.logging.Logger;

@Slf4j
final class CustomPlexusLogger extends DelegatingPlexusLogger
{
    private final LogTracker logTracker;

    public CustomPlexusLogger(Logger target, LogTracker logTracker)
    {
        super(target);
        this.logTracker = logTracker;
    }

    @Override
    public Logger getChildLogger(String name)
    {
        var original = super.getChildLogger(name);
        return new CustomPlexusLogger(original, logTracker);
    }

    @Override
    public void warn(String message, Throwable throwable)
    {
        super.warn(message, throwable);

        logTracker.trackWarning(message);
    }

    @Override
    public void warn(String message)
    {
        super.warn(message);

        logTracker.trackWarning(message);
    }
}
