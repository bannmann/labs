package dev.bannmann.labs.watchdog;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import org.codehaus.plexus.logging.LoggerManager;

@RequiredArgsConstructor
abstract class DelegatingLoggerManager implements LoggerManager
{
    @Delegate
    private final LoggerManager target;
}
