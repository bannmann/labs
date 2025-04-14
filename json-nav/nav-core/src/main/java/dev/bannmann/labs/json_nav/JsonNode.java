package dev.bannmann.labs.json_nav;

import com.google.errorprone.annotations.Immutable;

/**
 * Defines methods for {@code Ref} interfaces that represent JSON nodes. Also used for restricting the choice of types
 * for {@link AnyRef#asArray(Class)}.
 */
@Immutable
public sealed interface JsonNode permits ArrayRef, BooleanRef, NullRef, NumberRef, ObjectRef, StringRef
{
    String getRawJson();
}
