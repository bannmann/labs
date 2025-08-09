package dev.bannmann.labs.watchdog;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.apache.maven.plugin.logging.Log;

import dev.bannmann.labs.core.NullSafeLegacy;

@Slf4j
class CustomMavenPluginLog extends DelegatingMavenPluginLog
{
    private final LogTracker logTracker;

    @Inject
    public CustomMavenPluginLog(Log target, LogTracker logTracker)
    {
        super(target);
        this.logTracker = logTracker;
    }

    @Override
    public void warn(CharSequence content)
    {
        super.warn(content);

        logTracker.trackWarning(content);
    }

    @Override
    public void warn(CharSequence content, Throwable error)
    {
        super.warn(content, error);

        logTracker.trackWarning(content);
    }

    @Override
    public void warn(Throwable error)
    {
        super.warn(error);

        var messageOrNull = NullSafeLegacy.tryGet(error, Throwable::getMessage);
        logTracker.trackWarning(messageOrNull);
    }
}
