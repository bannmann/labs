package dev.bannmann.labs.json_nav;

import com.google.errorprone.annotations.Immutable;

/**
 * Represents a JSON string.
 */
@Immutable
public abstract non-sealed class StringRef extends TypedRef implements Value<String>
{
}
