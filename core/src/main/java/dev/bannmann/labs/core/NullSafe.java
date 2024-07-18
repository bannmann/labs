package dev.bannmann.labs.core;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import lombok.experimental.UtilityClass;

import dev.bannmann.labs.annotations.UpstreamCandidate;

@UpstreamCandidate("Mizool")
@UtilityClass
public class NullSafe
{
    public static <A, R> R tryGet(A a, Function<A, R> getter1)
    {
        if (a == null)
        {
            return null;
        }

        return getter1.apply(a);
    }

    public static <A, B, R> R tryGet(A a, Function<A, B> getter1, Function<B, R> getter2)
    {
        var b = tryGet(a, getter1);
        if (b == null)
        {
            return null;
        }

        return getter2.apply(b);
    }

    public static <A, B, C, R> R tryGet(A a, Function<A, B> getter1, Function<B, C> getter2, Function<C, R> getter3)
    {
        var c = tryGet(a, getter1, getter2);
        if (c == null)
        {
            return null;
        }

        return getter3.apply(c);
    }

    public static <A, B, C, D, R> R tryGet(
        A a, Function<A, B> getter1, Function<B, C> getter2, Function<C, D> getter3, Function<D, R> getter4)
    {
        var d = tryGet(a, getter1, getter2, getter3);
        if (d == null)
        {
            return null;
        }

        return getter4.apply(d);
    }

    public static <A, B, C, D, E, R> R tryGet(
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

    public static <A, B, C, D, E, F, R> R tryGet(
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

    public static <T> T coalesce(T t1, T t2)
    {
        return t1 != null
            ? t1
            : t2;
    }

    @SafeVarargs
    public static <T> T coalesce(T... t)
    {
        return Stream.of(t)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }
}
