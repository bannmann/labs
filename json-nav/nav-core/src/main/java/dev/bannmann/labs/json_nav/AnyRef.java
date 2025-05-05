package dev.bannmann.labs.json_nav;

import com.google.errorprone.annotations.Immutable;

/**
 * Represents a JSON value whose type is not known to the compiler. <br>
 * <br>
 * Its runtime type can be determined via the various {@code isFoo()} methods. Alternatively, use {@code instanceof}
 * checks with the {@code FooRef} classes from this package - the non-public implementation classes always implement
 * one of them.<br>
 * <br>
 * Consequently, an AnyRef could be turned into {@code FooRef} using a regular type cast. However, using the
 * {@code asFoo()} methods is generally preferable, particularly when dealing with arrays (due to the ability to specify
 * the ref type of its elements).
 */
@Immutable
public interface AnyRef
{
    default boolean isObject()
    {
        return false;
    }

    default boolean isArray()
    {
        return false;
    }

    default boolean isString()
    {
        return false;
    }

    default boolean isNumber()
    {
        return false;
    }

    default boolean isBoolean()
    {
        return false;
    }

    default boolean isNull()
    {
        return false;
    }

    default ObjectRef asObject()
    {
        throw new TypeMismatchException();
    }

    default <E extends TypedRef> ArrayRef<E> asArray(Class<E> elementClass)
    {
        throw new TypeMismatchException();
    }

    default ArrayRef<ObjectRef> asArrayOfObjects()
    {
        return asArray(ObjectRef.class);
    }

    default StringRef asString()
    {
        throw new TypeMismatchException();
    }

    default NumberRef asNumber()
    {
        throw new TypeMismatchException();
    }

    default BooleanRef asBoolean()
    {
        throw new TypeMismatchException();
    }

    default NullRef asNull()
    {
        throw new TypeMismatchException();
    }
}
