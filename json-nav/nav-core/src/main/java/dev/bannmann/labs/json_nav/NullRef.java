package dev.bannmann.labs.json_nav;

import static lombok.AccessLevel.PROTECTED;

import lombok.NoArgsConstructor;

import com.google.errorprone.annotations.Immutable;

/**
 * Represents JSON null.
 */
@Immutable
@NoArgsConstructor(access = PROTECTED)
public abstract non-sealed class NullRef extends TypedRef
{
}
