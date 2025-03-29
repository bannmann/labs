package dev.bannmann.labs.json_nav;

public sealed interface BooleanRef extends Value<Boolean>, JsonNode permits Constants.BooleanRefImpl
{
}
