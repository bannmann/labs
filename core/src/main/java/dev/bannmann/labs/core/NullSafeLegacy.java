package dev.bannmann.labs.core;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import lombok.experimental.UtilityClass;

import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

import dev.bannmann.labs.annotations.ImplementationNote;
import dev.bannmann.labs.annotations.SuppressWarningsRationale;

/**
 * Provides helper methods to succinctly deal with null values (NullAway friendly version).
 *
 * @see NullSafe
 */
@UtilityClass
@NullUnmarked
@ImplementationNote("This class is kept in sync with NullSafe. NullAway friendliness is achieved via `NullAwayInfo`.")
@SuppressWarnings("java:S2583")
@SuppressWarningsRationale("Sonar mistakenly thinks many `if` branches are unreachable")
public class NullSafeLegacy
{
    @SuppressWarnings("NullAway")
    @SuppressWarningsRationale(name = "NullAway", value = "NullAway does not yet observe @Nullable on generic types")
    public static <A extends @Nullable Object, R extends @Nullable Object> R tryGet(A a, Function<A, R> getter1)
    {
        if (a == null)
        {
            return null;
        }

        return getter1.apply(a);
    }

    @SuppressWarnings("NullAway")
    @SuppressWarningsRationale(name = "NullAway", value = "NullAway does not yet observe @Nullable on generic types")
    public static <A extends @Nullable Object, B extends @Nullable Object, R extends @Nullable Object> R tryGet(
        A a,
        Function<A, B> getter1,
        Function<B, R> getter2)
    {
        var b = tryGet(a, getter1);
        if (b == null)
        {
            return null;
        }

        return getter2.apply(b);
    }

    @SuppressWarnings("NullAway")
    @SuppressWarningsRationale(name = "NullAway", value = "NullAway does not yet observe @Nullable on generic types")
    public static <A extends @Nullable Object, B extends @Nullable Object, C extends @Nullable Object, R extends @Nullable Object> R tryGet(
        A a,
        Function<A, B> getter1,
        Function<B, C> getter2,
        Function<C, R> getter3)
    {
        var c = tryGet(a, getter1, getter2);
        if (c == null)
        {
            return null;
        }

        return getter3.apply(c);
    }

    @SuppressWarnings("NullAway")
    @SuppressWarningsRationale(name = "NullAway", value = "NullAway does not yet observe @Nullable on generic types")
    public static <A extends @Nullable Object, B extends @Nullable Object, C extends @Nullable Object, D extends @Nullable Object, R extends @Nullable Object> R tryGet(
        A a,
        Function<A, B> getter1,
        Function<B, C> getter2,
        Function<C, D> getter3,
        Function<D, R> getter4)
    {
        var d = tryGet(a, getter1, getter2, getter3);
        if (d == null)
        {
            return null;
        }

        return getter4.apply(d);
    }

    @SuppressWarnings("NullAway")
    @SuppressWarningsRationale(name = "NullAway", value = "NullAway does not yet observe @Nullable on generic types")
    public static <A extends @Nullable Object, B extends @Nullable Object, C extends @Nullable Object, D extends @Nullable Object, E extends @Nullable Object, R extends @Nullable Object> R tryGet(
        A a,
        Function<A, B> getter1,
        Function<B, C> getter2,
        Function<C, D> getter3,
        Function<D, E> getter4,
        Function<E, R> getter5)
    {
        var e = tryGet(a, getter1, getter2, getter3, getter4);
        if (e == null)
        {
            return null;
        }

        return getter5.apply(e);
    }

    @SuppressWarnings("NullAway")
    @SuppressWarningsRationale(name = "NullAway", value = "NullAway does not yet observe @Nullable on generic types")
    public static <A extends @Nullable Object, B extends @Nullable Object, C extends @Nullable Object, D extends @Nullable Object, E extends @Nullable Object, F extends @Nullable Object, R extends @Nullable Object> R tryGet(
        A a,
        Function<A, B> getter1,
        Function<B, C> getter2,
        Function<C, D> getter3,
        Function<D, E> getter4,
        Function<E, F> getter5,
        Function<F, R> getter6)
    {
        var f = tryGet(a, getter1, getter2, getter3, getter4, getter5);
        if (f == null)
        {
            return null;
        }

        return getter6.apply(f);
    }

    /**
     * Returns the first non-null argument, or null if all arguments are null.
     *
     * @param t1 first argument
     * @param t2 second argument
     * @param <T> type of arguments
     *
     * @return the first non-null argument, or {@code null} if all arguments are null
     */
    public static <T extends @Nullable Object> T coalesce(T t1, T t2)
    {
        return t1 != null
            ? t1
            : t2;
    }

    /**
     * Returns the first non-null argument, or null if all arguments are null.
     *
     * @param t1 first argument
     * @param t2 second argument
     * @param t more arguments (varargs)
     * @param <T> type of arguments
     *
     * @return the first non-null argument, or {@code null} if all arguments are null
     */
    @SafeVarargs
    @SuppressWarnings({ "NullAway", "java:S2589" })
    @SuppressWarningsRationale(name = "NullAway", value = "NullAway does not yet observe @Nullable on generic types")
    @SuppressWarningsRationale(name = "java:S2589", value = "Sonar thinks `t1 != null` is always true")
    public static <T extends @Nullable Object> T coalesce(T t1, T t2, T... t)
    {
        if (t1 != null)
        {
            return t1;
        }

        if (t2 != null)
        {
            return t2;
        }

        return Stream.of(t)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }
}
