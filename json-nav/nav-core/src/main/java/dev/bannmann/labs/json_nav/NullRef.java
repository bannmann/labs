package dev.bannmann.labs.json_nav;

import com.google.errorprone.annotations.Immutable;

@Immutable
public abstract sealed class NullRef implements JsonNode permits Constants.NullRefImpl
{
}
