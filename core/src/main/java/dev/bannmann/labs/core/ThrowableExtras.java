package dev.bannmann.labs.core;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import lombok.experimental.UtilityClass;

import com.google.common.base.Throwables;

@UtilityClass
public class ThrowableExtras
{
    public static <T> Stream<T> filterCausalChain(Throwable e, Class<T> exceptionClass)
    {
        return Throwables.getCausalChain(e)
            .stream()
            .filter(exceptionClass::isInstance)
            .map(exceptionClass::cast);
    }

    public static <T> Optional<T> findFirstInChain(Throwable e, Class<T> exceptionClass)
    {
        return filterCausalChain(e, exceptionClass).findFirst();
    }

    public static <T> Optional<T> findFirstInChain(Throwable e, Class<T> exceptionClass, Predicate<T> predicate)
    {
        return findFirstInChain(e, exceptionClass).filter(predicate);
    }

    public static <T> boolean anyCauseMatches(Throwable e, Class<T> exceptionClass)
    {
        return findFirstInChain(e, exceptionClass).isPresent();
    }

    public static <T> boolean anyCauseMatches(Throwable e, Class<T> exceptionClass, Predicate<T> predicate)
    {
        return findFirstInChain(e, exceptionClass, predicate).isPresent();
    }

    public static boolean anyCauseInstanceOf(Throwable e, Class<?>... exceptionClasses)
    {
        return Throwables.getCausalChain(e)
            .stream()
            .anyMatch(throwable -> Arrays.stream(exceptionClasses)
                .anyMatch(exceptionClass -> exceptionClass.isInstance(throwable)));
    }
}
