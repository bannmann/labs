package dev.bannmann.labs.core.internal;

import org.kohsuke.MetaInfServices;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.uber.nullaway.LibraryModels;
import dev.bannmann.labs.annotations.VisibleForServiceLoader;
import dev.bannmann.labs.core.NullSafeLegacy;

/**
 * Helps to interpret method signatures within this library when downstream projects use NullAway.
 */
@VisibleForServiceLoader
@MetaInfServices
public final class NullAwayInfo implements LibraryModels
{
    @Override
    public ImmutableSetMultimap<MethodRef, Integer> failIfNullParameters()
    {
        return ImmutableSetMultimap.of();
    }

    @Override
    public ImmutableSetMultimap<MethodRef, Integer> explicitlyNullableParameters()
    {
        return ImmutableSetMultimap.of(MethodRef.methodRef(NullSafeLegacy.class.getName(), "tryGet"), 0);
    }

    @Override
    public ImmutableSetMultimap<MethodRef, Integer> nonNullParameters()
    {
        return ImmutableSetMultimap.of();
    }

    @Override
    public ImmutableSetMultimap<MethodRef, Integer> nullImpliesTrueParameters()
    {
        return ImmutableSetMultimap.of();
    }

    @Override
    public ImmutableSetMultimap<MethodRef, Integer> nullImpliesFalseParameters()
    {
        return ImmutableSetMultimap.of();
    }

    @Override
    public ImmutableSetMultimap<MethodRef, Integer> nullImpliesNullParameters()
    {
        return ImmutableSetMultimap.of();
    }

    @Override
    public ImmutableSet<MethodRef> nullableReturns()
    {
        return ImmutableSet.of();
    }

    @Override
    public ImmutableSet<MethodRef> nonNullReturns()
    {
        return ImmutableSet.of();
    }

    @Override
    public ImmutableSetMultimap<MethodRef, Integer> castToNonNullMethods()
    {
        return ImmutableSetMultimap.of();
    }
}
