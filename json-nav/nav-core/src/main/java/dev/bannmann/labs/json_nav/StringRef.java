package dev.bannmann.labs.json_nav;

import static lombok.AccessLevel.PROTECTED;

import lombok.NoArgsConstructor;

import com.google.errorprone.annotations.Immutable;

/**
 * Represents a JSON string.
 */
@Immutable
@NoArgsConstructor(access = PROTECTED)
public abstract non-sealed class StringRef extends TypedRef implements Value<String>
{
}
