package dev.bannmann.labs.json_nav;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
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

    /**
     * Navigates to the only element of this array.
     *
     * @return the element
     *
     * @throws UnexpectedElementException if the array contains more than one element
     * @throws MissingElementException if the array is empty
     */
    public final T onlyElement()
    {
        var iterator = iterator();
        try
        {
            T result = iterator.next();
            if (iterator.hasNext())
            {
                throw new UnexpectedElementException();
            }

            return result;
        }
        catch (NoSuchElementException e)
        {
            throw new MissingElementException(e);
        }
    }

    public final <R> R map(Function<ArrayRef<T>, R> function)
    {
        return function.apply(this);
    }
}
