package dev.bannmann.labs.json_nav;

/**
 * Defines methods for {@code Ref} interfaces that represent JSON nodes. Also used for restricting the choice of types
 * for {@link AnyRef#asArray(Class)}.
 */
public sealed interface JsonNode permits ArrayRef, BooleanRef, NullRef, NumberRef, ObjectRef, StringRef
{
    String getRawJson();
}
