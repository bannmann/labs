package dev.bannmann.labs.json_nav;

import java.util.List;
import java.util.stream.Stream;

import com.google.errorprone.annotations.Immutable;

/**
 * Represents a JSON array.
 *
 * @param <T> the ref type of the array's elements
 */
@Immutable
public abstract non-sealed class ArrayRef<T extends TypedRef> extends TypedRef implements Iterable<T>
{
    public abstract boolean isEmpty();

    public abstract Stream<T> stream();

    public abstract List<T> toList();

    public abstract int size();
}
