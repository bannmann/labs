package dev.bannmann.labs.core;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import lombok.experimental.UtilityClass;

import org.jspecify.annotations.Nullable;

import dev.bannmann.labs.annotations.SuppressWarningsRationale;
import dev.bannmann.labs.annotations.UpstreamCandidate;

@UpstreamCandidate("Mizool")
@UtilityClass
public class NullSafe
{
    @SuppressWarnings("NullAway")
    @SuppressWarningsRationale("NullAway does not yet observe @Nullable on generic types")
    public static <A extends @Nullable Object, R extends @Nullable Object> R tryGet(A a, Function<A, R> getter1)
    {
        if (a == null)
        {
            return null;
        }

        return getter1.apply(a);
    }

    @SuppressWarnings("NullAway")
    @SuppressWarningsRationale("NullAway does not yet observe @Nullable on generic types")
    public static <A extends @Nullable Object, B extends @Nullable Object, R extends @Nullable Object> R tryGet(
        A a, Function<A, B> getter1, Function<B, R> getter2)
    {
        var b = tryGet(a, getter1);
        if (b == null)
        {
            return null;
        }

        return getter2.apply(b);
    }

    @SuppressWarnings({ "NullAway", "ConstantValue" })
    @SuppressWarningsRationale(
        "NullAway does not yet observe @Nullable on generic types; 'ConstantValue' because IntelliJ thinks c is never null")
    public static <A extends @Nullable Object, B extends @Nullable Object, C extends @Nullable Object, R extends @Nullable Object> R tryGet(
        A a, Function<A, B> getter1, Function<B, C> getter2, Function<C, R> getter3)
    {
        var c = tryGet(a, getter1, getter2);
        if (c == null)
        {
            return null;
        }

        return getter3.apply(c);
    }

    @SuppressWarnings({ "NullAway", "ConstantValue" })
    @SuppressWarningsRationale(
        "NullAway does not yet observe @Nullable on generic types; 'ConstantValue' because IntelliJ thinks d is never null")
    public static <A extends @Nullable Object, B extends @Nullable Object, C extends @Nullable Object, D extends @Nullable Object, R extends @Nullable Object> R tryGet(
        A a, Function<A, B> getter1, Function<B, C> getter2, Function<C, D> getter3, Function<D, R> getter4)
    {
        var d = tryGet(a, getter1, getter2, getter3);
        if (d == null)
        {
            return null;
        }

        return getter4.apply(d);
    }

    @SuppressWarnings({ "NullAway", "ConstantValue" })
    @SuppressWarningsRationale(
        "NullAway does not yet observe @Nullable on generic types; 'ConstantValue' because IntelliJ thinks e is never null")
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

    @SuppressWarnings({ "NullAway", "ConstantValue" })
    @SuppressWarningsRationale(
        "NullAway does not yet observe @Nullable on generic types; 'ConstantValue' because IntelliJ thinks f is never null")
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

    public static <T extends @Nullable Object> T coalesce(T t1, T t2)
    {
        return t1 != null
            ? t1
            : t2;
    }

    @SafeVarargs
    @SuppressWarnings({ "NullAway", "ConstantValue" })
    @SuppressWarningsRationale(
        "NullAway does not yet observe @Nullable on generic types; 'ConstantValue' because IntelliJ thinks Objects::nonNull is superfluous")
    public static <T extends @Nullable Object> T coalesce(T... t)
    {
        return Stream.of(t)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }
}
