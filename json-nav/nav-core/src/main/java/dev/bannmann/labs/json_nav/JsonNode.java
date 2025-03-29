package dev.bannmann.labs.json_nav;

/**
 * Marker interface for {@code Ref} interfaces that represent JSON nodes. Used for restricting the choice of types for
 * {@link AnyRef#asArray(Class)}.
 */
public sealed interface JsonNode permits ArrayRef, BooleanRef, NullRef, NumberRef, ObjectRef, StringRef
{
}
