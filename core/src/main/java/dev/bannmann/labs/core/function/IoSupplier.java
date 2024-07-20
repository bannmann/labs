package dev.bannmann.labs.core.function;

import java.io.IOException;

import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface IoSupplier<T extends @Nullable Object>
{
    T get() throws IOException;
}
