package dev.bannmann.labs.core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;

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

    /**
     * Collects the last n input elements (or fewer) into a list.
     *
     * @param <T> element type
     * @param limit the maximum number of elements. Must be greater than 0.
     *
     * @throws IllegalArgumentException if {@code limit} is 0 or negative
     */
    public static <T> Collector<T, ?, List<T>> lastN(int limit)
    {
        if (limit < 1)
        {
            throw new IllegalArgumentException("Limit must be greater than 0");
        }

        // from https://stackoverflow.com/a/30477722/7641
        return Collector.of(ArrayDeque::new, (deque, e) -> {
            if (deque.size() == limit)
            {
                deque.pollFirst();
            }
            deque.add(e);
        }, (BinaryOperator<Deque<T>>) (deque1, deque2) -> {
            while (deque2.size() < limit && !deque1.isEmpty())
            {
                deque2.addFirst(deque1.pollLast());
            }
            return deque2;
        }, ArrayList::new);
    }
}
