package dev.bannmann.labs.json_nav;

import static lombok.AccessLevel.PROTECTED;

import lombok.NoArgsConstructor;

import com.google.errorprone.annotations.Immutable;

/**
 * Represents a JSON boolean.
 */
@Immutable
@NoArgsConstructor(access = PROTECTED)
public abstract non-sealed class BooleanRef extends TypedRef implements Value<Boolean>
{
}
