package dev.bannmann.labs.json_nav;

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

    default <E extends JsonNode> ArrayRef<E> asArray(Class<E> elementClass)
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
