package dev.bannmann.labs.json_nav;

import com.google.errorprone.annotations.Immutable;

@Immutable
public sealed interface BooleanRef extends Value<Boolean>, JsonNode permits Constants.BooleanRefImpl
{
}
