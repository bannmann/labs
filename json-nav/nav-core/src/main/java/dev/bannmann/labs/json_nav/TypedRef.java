package dev.bannmann.labs.json_nav;

import static lombok.AccessLevel.PACKAGE;

import lombok.NoArgsConstructor;

import com.google.errorprone.annotations.Immutable;

/**
 * Base class for classes that represent values in a JSON document.
 */
@Immutable
@NoArgsConstructor(access = PACKAGE)
public abstract sealed class TypedRef permits ArrayRef, BooleanRef, NullRef, NumberRef, ObjectRef, StringRef
{
    public abstract String getRawJson();
}
