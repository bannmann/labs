package dev.bannmann.labs.json_nav;

import com.google.errorprone.annotations.Immutable;

@Immutable
public sealed interface NullRef extends JsonNode permits Constants.NullRefImpl
{
}
