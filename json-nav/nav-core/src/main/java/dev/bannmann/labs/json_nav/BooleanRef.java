package dev.bannmann.labs.json_nav;

import com.google.errorprone.annotations.Immutable;

/**
 * Represents a JSON boolean.
 */
@Immutable
public abstract sealed class BooleanRef extends TypedRef implements Value<Boolean> permits Constants.BooleanRefImpl
{
}
