package dev.bannmann.labs.core.function;

import java.io.IOException;

@FunctionalInterface
public interface IoRunnable
{
    void run() throws IOException;
}
