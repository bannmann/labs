package com.github.bannmann.labs.core;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import lombok.experimental.UtilityClass;

import com.github.bannmann.labs.annotations.UpstreamCandidate;

@UpstreamCandidate("Mizool")
@UtilityClass
public class NullSafe
{
    public <A, R> R get(A a, Function<A, R> getter1)
    {
        if (a == null)
        {
            return null;
        }

        return getter1.apply(a);
    }

    public <A, B, R> R get(A a, Function<A, B> getter1, Function<B, R> getter2)
    {
        var b = get(a, getter1);
        if (b == null)
        {
            return null;
        }

        return getter2.apply(b);
    }

    public <A, B, C, R> R get(A a, Function<A, B> getter1, Function<B, C> getter2, Function<C, R> getter3)
    {
        var c = get(a, getter1, getter2);
        if (c == null)
        {
            return null;
        }

        return getter3.apply(c);
    }

    public <A, B, C, D, R> R get(
        A a, Function<A, B> getter1, Function<B, C> getter2, Function<C, D> getter3, Function<D, R> getter4)
    {
        var d = get(a, getter1, getter2, getter3);
        if (d == null)
        {
            return null;
        }

        return getter4.apply(d);
    }

    public <A, B, C, D, E, R> R get(
        A a,
        Function<A, B> getter1,
        Function<B, C> getter2,
        Function<C, D> getter3,
        Function<D, E> getter4,
        Function<E, R> getter5)
    {
        var e = get(a, getter1, getter2, getter3, getter4);
        if (e == null)
        {
            return null;
        }

        return getter5.apply(e);
    }

    public <A, B, C, D, E, F, R> R get(
        A a,
        Function<A, B> getter1,
        Function<B, C> getter2,
        Function<C, D> getter3,
        Function<D, E> getter4,
        Function<E, F> getter5,
        Function<F, R> getter6)
    {
        var f = get(a, getter1, getter2, getter3, getter4, getter5);
        if (f == null)
        {
            return null;
        }

        return getter6.apply(f);
    }

    public <T> T coalesce(T t1, T t2)
    {
        return t1 != null
            ? t1
            : t2;
    }

    @SafeVarargs
    public <T> T coalesce(T... t)
    {
        return Stream.of(t)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }
}
