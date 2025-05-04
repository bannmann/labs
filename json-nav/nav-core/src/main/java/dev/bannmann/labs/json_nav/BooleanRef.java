package dev.bannmann.labs.json_nav;

import com.google.errorprone.annotations.Immutable;

@Immutable
public abstract sealed class BooleanRef implements Value<Boolean>, JsonNode permits Constants.BooleanRefImpl
{
}
