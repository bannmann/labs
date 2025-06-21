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

    /**
     * @see #readString()
     */
    default StringRef asString()
    {
        throw new TypeMismatchException();
    }

    /**
     * @see #asString()
     */
    default String readString()
    {
        return asString().read();
    }

    default NumberRef asNumber()
    {
        throw new TypeMismatchException();
    }

    /**
     * @see #readBoolean()
     */
    default BooleanRef asBoolean()
    {
        throw new TypeMismatchException();
    }

    /**
     * @see #asBoolean()
     */
    default boolean readBoolean()
    {
        return asBoolean().read();
    }

    default NullRef asNull()
    {
        throw new TypeMismatchException();
    }
}
