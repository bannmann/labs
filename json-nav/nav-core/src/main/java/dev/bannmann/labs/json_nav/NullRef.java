package dev.bannmann.labs.json_nav;

import com.google.errorprone.annotations.Immutable;

/**
 * Represents JSON null.
 */
@Immutable
public abstract sealed class NullRef extends TypedRef permits Constants.NullRefImpl
{
}
