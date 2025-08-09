package dev.bannmann.labs.watchdog;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import org.codehaus.plexus.logging.Logger;

import dev.bannmann.labs.annotations.SuppressWarningsRationale;

@RequiredArgsConstructor
abstract class DelegatingPlexusLogger implements Logger
{
    @Delegate
    @SuppressWarnings("java:S1312")
    @SuppressWarningsRationale("The usual name 'log' would detract from the fact that this is a delegation target and the sole purpose of this class")
    protected final Logger target;
}
