package dev.bannmann.labs.watchdog;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import org.apache.maven.plugin.logging.Log;

@RequiredArgsConstructor
abstract class DelegatingMavenPluginLog implements Log
{
    @Delegate
    private final Log target;
}
