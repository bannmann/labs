package dev.bannmann.labs.json_nav;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.github.mizool.core.exception.CodeInconsistencyException;
import dev.bannmann.labs.annotations.SuppressWarningsRationale;

/**
 * Contains a value retrieved or mapped from a JSON value. <br>
 * <br>
 * This type exists to facilitate call chains that map values. It can be considered a counterpart of {@link Optional},
 * but unlike that class, {@code Value} is intended for values that can never be {@code null}.
 *
 * @param <T> the type of the contained value
 */
public interface Value<T>
{
    T read();

    /**
     * @throws IllegalArgumentException if the given mapper returns {@code null}
     */
    default <R> Value<R> map(Function<T, R> mapper)
    {
        T contents = obtainContents();
        R mapped = mapper.apply(contents);
        R result = verifyNotNull(mapped, IllegalArgumentException::new);
        return () -> result;
    }

    private T obtainContents()
    {
        T contents = read();
        return verifyNotNull(contents, CodeInconsistencyException::new);
    }

    @SuppressWarnings("java:S2583")
    @SuppressWarningsRationale(
        "As implementations and mappers come from other artifacts, we cannot rely on their null safety")
    private <X> X verifyNotNull(X contents, Supplier<RuntimeException> exceptionSupplier)
    {
        //noinspection ConstantValue
        if (contents == null)
        {
            throw exceptionSupplier.get();
        }
        return contents;
    }

    /**
     * Calls a mapper that may or may not produce a value. <br>
     * <br>
     * If you do not need an {@link Optional}, you can instead use {@link #map(Function)}.
     *
     * @throws IllegalArgumentException if the given mapper returns {@code null}
     */
    default <R> Optional<R> mapToOptional(Function<T, Optional<R>> mapper)
    {
        T contents = obtainContents();
        Optional<R> mapped = mapper.apply(contents);
        return verifyNotNull(mapped, IllegalArgumentException::new);
    }

    /**
     * Gets an {@code Optional} for the contained value.
     *
     * @return a non-empty {@code Optional}
     */
    default Optional<T> toOptional()
    {
        T contents = obtainContents();
        return Optional.of(contents);
    }

    /**
     * Gets a stream for the contained value.
     *
     * @return a stream that emits one element
     */
    default Stream<T> stream()
    {
        T contents = obtainContents();
        return Stream.of(contents);
    }

    /**
     * Gets an {@code Optional} for the contained value if it matches the given predicate. <br>
     * <br>
     * Equivalent to calling {@code toOptional().filter(predicate)}.
     *
     * @param predicate the predicate to apply to the contained value
     *
     * @return a non-empty {@code Optional} with a value matching the given predicate, otherwise an empty
     * {@code Optional}
     *
     * @throws NullPointerException if the predicate is {@code null}
     */
    default Optional<T> filter(Predicate<? super T> predicate)
    {
        return toOptional().filter(predicate);
    }
}
