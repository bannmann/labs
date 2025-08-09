package dev.bannmann.labs.watchdog;

import lombok.extern.slf4j.Slf4j;

import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;

@Slf4j
class CustomLoggerManager extends DelegatingLoggerManager
{
    private final LogTracker logTracker;

    public CustomLoggerManager(LoggerManager target, LogTracker logTracker)
    {
        super(target);
        this.logTracker = logTracker;
    }

    @Override
    public Logger getLoggerForComponent(String role)
    {
        var original = super.getLoggerForComponent(role);
        return new CustomPlexusLogger(original, logTracker);
    }

    @Override
    public Logger getLoggerForComponent(String role, String hint)
    {
        var original = super.getLoggerForComponent(role, hint);
        return new CustomPlexusLogger(original, logTracker);
    }
}
