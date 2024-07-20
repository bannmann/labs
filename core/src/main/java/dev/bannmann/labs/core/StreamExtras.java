package dev.bannmann.labs.core;

import java.util.function.BinaryOperator;
import java.util.function.Supplier;

import lombok.experimental.UtilityClass;

import org.jspecify.annotations.Nullable;

import com.google.common.collect.MoreCollectors;
import dev.bannmann.labs.annotations.UpstreamCandidate;

@UpstreamCandidate("Mizool")
@UtilityClass
public class StreamExtras
{
    /**
     * Use with {@link java.util.stream.Stream#reduce(BinaryOperator)} to ensure there is at most one element. Note that
     * if you want an exception in case no element exists, you should instead use Guava's
     * {@link MoreCollectors#onlyElement()}.
     *
     * @param <T> element type
     *
     * @return an operator to use with {@link java.util.stream.Stream#reduce(BinaryOperator)}
     */
    public static <T extends @Nullable Object> BinaryOperator<T> atMostOne()
    {
        return atMostOneThrowing(IllegalArgumentException::new);
    }

    /**
     * Use with {@link java.util.stream.Stream#reduce(BinaryOperator)} to ensure there is at most one element. Note that
     * if you want an exception in case no element exists, you should instead use Guava's
     * {@link MoreCollectors#onlyElement()}.
     *
     * @param <T> element type
     * @param <E> type of the exception to throw if more than one element is encountered
     * @param exception thrown if more than one element is encountered
     */
    public static <T extends @Nullable Object, E extends RuntimeException> BinaryOperator<T> atMostOneThrowing(Supplier<E> exception)
    {
        return (element, otherElement) -> {
            throw exception.get();
        };
    }
}
