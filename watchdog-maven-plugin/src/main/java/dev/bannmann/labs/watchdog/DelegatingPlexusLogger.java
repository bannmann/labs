package dev.bannmann.labs.watchdog;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import org.codehaus.plexus.logging.Logger;

@RequiredArgsConstructor
abstract class DelegatingPlexusLogger implements Logger
{
    @Delegate
    protected final Logger target;
}
