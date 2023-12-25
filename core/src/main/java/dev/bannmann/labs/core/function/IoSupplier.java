package dev.bannmann.labs.core.function;

import java.io.IOException;

@FunctionalInterface
public interface IoSupplier<T>
{
    T get() throws IOException;
}
