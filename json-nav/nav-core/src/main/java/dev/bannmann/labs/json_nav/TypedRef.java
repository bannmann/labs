package dev.bannmann.labs.json_nav;

import com.google.errorprone.annotations.Immutable;

/**
 * Base class for classes that represent values in a JSON document.
 */
@Immutable
public abstract sealed class TypedRef permits ArrayRef, BooleanRef, NullRef, NumberRef, ObjectRef, StringRef
{
    public abstract String getRawJson();
}
