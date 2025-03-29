package dev.bannmann.labs.json_nav;

import java.util.Optional;
import java.util.function.Function;

public interface Value<T>
{
    T read();

    default <R> Value<R> map(Function<T, R> mapper)
    {
        R mapped = mapper.apply(read());
        return () -> mapped;
    }

    default <R> Optional<R> retrieveOptionalValueVia(Function<T, Optional<R>> mapper)
    {
        return mapper.apply(read());
    }
}
