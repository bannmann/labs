package dev.bannmann.labs.json_nav;

import java.util.Optional;
import java.util.function.Function;

import com.google.errorprone.annotations.Immutable;

@Immutable
public non-sealed interface ObjectRef extends JsonNode
{
    /**
     * Gets a JSON value that the given name maps to.<br>
     * <br>
     * Note that this method distinguishes between a non-existing mapping (empty {@code Optional}) and a mapping to a
     * JSON {@code null} literal ({@code Optional} containing a {@link NullRef}).
     *
     * @param name the name of the desired property.
     *
     * @return an {@code Optional} containing a ref to the JSON value that the given name maps to (which may be a {@link NullRef}), or an empty {@code Optional} if there is no such mapping.
     */
    Optional<AnyRef> tryGetAny(String name);

    /**
     * Gets a JSON value that the given name maps to.<br>
     * <br>
     * Note that this method distinguishes between a non-existing mapping (empty {@code Optional}) and a mapping to a
     * JSON {@code null} literal ({@code Optional} containing a {@link NullRef}).
     *
     * @param name the name of the desired property.
     *
     * @return an {@code Optional} containing a ref to the JSON value that the given name maps to (which may be a {@link NullRef}), or an empty {@code Optional} if there is no such mapping.
     *
     * @deprecated This method was renamed. Use {@link #tryGetAny(String)} instead
     */
    @Deprecated(forRemoval = true)
    default Optional<AnyRef> tryGet(String name)
    {
        return tryGetAny(name);
    }

    /**
     * Gets a JSON Boolean that the given name maps to.<br>
     * <br>
     * This method does not distinguish between missing mappings and mappings to JSON {@code null} literals. If that
     * distinction is required, use {@link #tryGetAny(String)} instead.
     *
     * @param name the name of the desired property.
     *
     * @return an {@code Optional} containing a ref to the JSON Boolean that the given name maps to, or an empty {@code Optional}
     *
     * @throws TypeMismatchException if the given name is mapped to a value that is neither a boolean nor a {@code null} literal
     */
    default Optional<BooleanRef> tryGetBoolean(String name)
    {
        return tryGetAny(name).filter(NullRef.EXCLUDE)
            .map(AnyRef::asBoolean);
    }

    /**
     * Gets a JSON Number that the given name maps to.<br>
     * <br>
     * This method does not distinguish between missing mappings and mappings to JSON {@code null} literals. If that
     * distinction is required, use {@link #tryGetAny(String)} instead.
     *
     * @param name the name of the desired property.
     *
     * @return an {@code Optional} containing a ref to the JSON Number that the given name maps to, or an empty {@code Optional}
     *
     * @throws TypeMismatchException if the given name is mapped to a value that is neither a number nor a {@code null} literal
     */
    default Optional<NumberRef> tryGetNumber(String name)
    {
        return tryGetAny(name).filter(NullRef.EXCLUDE)
            .map(AnyRef::asNumber);
    }

    /**
     * Gets a JSON String that the given name maps to.<br>
     * <br>
     * This method does not distinguish between missing mappings and mappings to JSON {@code null} literals. If that
     * distinction is required, use {@link #tryGetAny(String)} instead.
     *
     * @param name the name of the desired property.
     *
     * @return an {@code Optional} containing a ref to the JSON String that the given name maps to, or an empty {@code Optional}
     *
     * @throws TypeMismatchException if the given name is mapped to a value that is neither a string nor a {@code null} literal
     */
    default Optional<StringRef> tryGetString(String name)
    {
        return tryGetAny(name).filter(NullRef.EXCLUDE)
            .map(AnyRef::asString);
    }

    /**
     * Gets a JSON Object that the given name maps to.<br>
     * <br>
     * This method does not distinguish between missing mappings and mappings to JSON {@code null} literals. If that
     * distinction is required, use {@link #tryGetAny(String)} instead.
     *
     * @param name the name of the desired property.
     *
     * @return an {@code Optional} containing a ref to the JSON Object that the given name maps to, or an empty {@code Optional}
     *
     * @throws TypeMismatchException if the given name is mapped to a value that is neither an object nor a {@code null} literal
     */
    default Optional<ObjectRef> tryGetObject(String name)
    {
        return tryGetAny(name).filter(NullRef.EXCLUDE)
            .map(AnyRef::asObject);
    }

    /**
     * Gets a JSON Array that the given name maps to.<br>
     * <br>
     * This method does not distinguish between missing mappings and mappings to JSON {@code null} literals. If that
     * distinction is required, use {@link #tryGetAny(String)} instead.
     *
     * @param name the name of the desired property.
     *
     * @return an {@code Optional} containing a ref to the JSON Array that the given name maps to, or an empty {@code Optional}
     *
     * @throws TypeMismatchException if the given name is mapped to a value that is neither an array nor a {@code null} literal
     */
    default Optional<ArrayRef<ObjectRef>> tryGetArrayOfObjects(String name)
    {
        return tryGetAny(name).filter(NullRef.EXCLUDE)
            .map(AnyRef::asArrayOfObjects);
    }

    /**
     * Gets a JSON Array that the given name maps to.<br>
     * <br>
     * This method does not distinguish between missing mappings and mappings to JSON {@code null} literals. If that
     * distinction is required, use {@link #tryGetAny(String)} instead.
     *
     * @param <E> the type of array elements
     * @param elementClass the class to use for the array elements
     * @param name the name of the desired property.
     *
     * @return an {@code Optional} containing a ref to the JSON Array that the given name maps to, or an empty {@code Optional}
     *
     * @throws TypeMismatchException if the given name is mapped to a value that is neither an array nor a {@code null} literal
     */
    default <E extends JsonNode> Optional<ArrayRef<E>> tryGetArray(Class<E> elementClass, String name)
    {
        return tryGetAny(name).filter(NullRef.EXCLUDE)
            .map(anyRef -> anyRef.asArray(elementClass));
    }

    /**
     * Navigates to the given property name.
     *
     * @param name the property name
     *
     * @return the new ref, never {@code null}. Note, however, that the ref returned may be a {@link NullRef}.
     *
     * @throws MissingElementException if the given name is not mapped to a value
     *
     * @deprecated This method was renamed. Use {@link #obtainAny(String)} instead
     */
    @Deprecated(forRemoval = true)
    default AnyRef obtain(String name)
    {
        return obtainAny(name);
    }

    /**
     * Navigates to the given property name.
     *
     * @param name the property name
     *
     * @return the new ref, never {@code null}. Note, however, that the ref returned may be a {@link NullRef}.
     *
     * @throws MissingElementException if the given name is not mapped to a value
     */
    default AnyRef obtainAny(String name)
    {
        return tryGetAny(name).orElseThrow(MissingElementException::new);
    }

    /**
     * Navigates to a property of a nested object. <br>
     * <br>
     * Calling {@code obtain("a", "b", "c")} is equivalent to {@code obtainObject("a").obtainObject("b").obtain("c")}.
     *
     * @param firstLevel the property name on the first level
     * @param moreLevels property names to use on subsequent levels
     *
     * @return the new ref, never {@code null}. Note, however, that the ref returned may be a {@link NullRef}.
     *
     * @throws MissingElementException if any of the names given is not mapped to a value
     * @throws TypeMismatchException if any name except the last one is mapped to a value that is not an object, e.g. a {@code null} literal
     *
     * @deprecated This method was renamed. Use {@link #obtainAny(String, String...)} instead
     */
    @Deprecated(forRemoval = true)
    default AnyRef obtain(String firstLevel, String... moreLevels)
    {
        return obtainAny(firstLevel, moreLevels);
    }

    /**
     * Navigates to a property of a nested object. <br>
     * <br>
     * Calling {@code obtainAny("a", "b", "c")} is equivalent to {@code obtainObject("a").obtainObject("b").obtainAny("c")}.
     *
     * @param firstLevel the property name on the first level
     * @param moreLevels property names to use on subsequent levels
     *
     * @return the new ref, never {@code null}. Note, however, that the ref returned may be a {@link NullRef}.
     *
     * @throws MissingElementException if any of the names given is not mapped to a value
     * @throws TypeMismatchException if any name except the last one is mapped to a value that is not an object, e.g. a {@code null} literal
     */
    default AnyRef obtainAny(String firstLevel, String... moreLevels)
    {
        if (moreLevels.length == 0)
        {
            return obtainAny(firstLevel);
        }

        ObjectRef currentObject = obtainObject(firstLevel);
        for (int i = 0; i < moreLevels.length - 1; i++)
        {
            currentObject = currentObject.obtainObject(moreLevels[i]);
        }

        return currentObject.obtainAny(moreLevels[moreLevels.length - 1]);
    }

    /**
     * Navigates to a boolean property. <br>
     * <br>
     * This is equivalent to calling {@code obtainAny(name).asBoolean()}.
     *
     * @param name the property name
     *
     * @return the new ref, never {@code null}.
     *
     * @throws MissingElementException if the given name is not mapped to a value
     * @throws TypeMismatchException if the given name is mapped to a value that is not a boolean, e.g. a {@code null} literal
     */
    default BooleanRef obtainBoolean(String name)
    {
        return obtainAny(name).asBoolean();
    }

    /**
     * Navigates to a boolean property of a nested object. <br>
     * <br>
     * Calling {@code obtainBoolean("a", "b", "c")} is equivalent to {@code obtainObject("a").obtainObject("b").obtainBoolean("c")}.
     *
     * @param firstLevel the property name on the first level
     * @param moreLevels property names to use on subsequent levels
     *
     * @return the new ref, never {@code null}.
     *
     * @throws MissingElementException if any of the names given is not mapped to a value, or to a {@code null} literal
     * @throws TypeMismatchException if the last name is mapped to a value that is not a boolean, or if any name except the last one is mapped to a value that is not an object, e.g. a {@code null} literal
     */
    default BooleanRef obtainBoolean(String firstLevel, String... moreLevels)
    {
        return obtainAny(firstLevel, moreLevels).asBoolean();
    }

    /**
     * Navigates to a number property. <br>
     * <br>
     * This is equivalent to calling {@code obtainAny(name).asNumber()}.
     *
     * @param name the property name
     *
     * @return the new ref, never {@code null}.
     *
     * @throws MissingElementException if the given name is not mapped to a value
     * @throws TypeMismatchException if the given name is mapped to a value that is not a number, e.g. a {@code null} literal
     */
    default NumberRef obtainNumber(String name)
    {
        return obtainAny(name).asNumber();
    }

    /**
     * Navigates to a number property of a nested object. <br>
     * <br>
     * Calling {@code obtainNumber("a", "b", "c")} is equivalent to {@code obtainObject("a").obtainObject("b").obtainNumber("c")}.
     *
     * @param firstLevel the property name on the first level
     * @param moreLevels property names to use on subsequent levels
     *
     * @return the new ref, never {@code null}.
     *
     * @throws MissingElementException if any of the names given is not mapped to a value, or to a {@code null} literal
     * @throws TypeMismatchException if the last name is mapped to a value that is not a number, or if any name except the last one is mapped to a value that is not an object, e.g. a {@code null} literal
     */
    default NumberRef obtainNumber(String firstLevel, String... moreLevels)
    {
        return obtainAny(firstLevel, moreLevels).asNumber();
    }

    /**
     * Navigates to a string property. <br>
     * <br>
     * This is equivalent to calling {@code obtainAny(name).asString()}.
     *
     * @param name the property name
     *
     * @return the new ref, never {@code null}.
     *
     * @throws MissingElementException if the given name is not mapped to a value
     * @throws TypeMismatchException if the given name is mapped to a value that is not a string, e.g. a {@code null} literal
     */
    default StringRef obtainString(String name)
    {
        return obtainAny(name).asString();
    }

    /**
     * Navigates to a string property of a nested object. <br>
     * <br>
     * Calling {@code obtainString("a", "b", "c")} is equivalent to {@code obtainObject("a").obtainObject("b").obtainString("c")}.
     *
     * @param firstLevel the property name on the first level
     * @param moreLevels property names to use on subsequent levels
     *
     * @return the new ref, never {@code null}.
     *
     * @throws MissingElementException if any of the names given is not mapped to a value, or to a {@code null} literal
     * @throws TypeMismatchException if the last name is mapped to a value that is not a string, or if any name except the last one is mapped to a value that is not an object, e.g. a {@code null} literal
     */
    default StringRef obtainString(String firstLevel, String... moreLevels)
    {
        return obtainAny(firstLevel, moreLevels).asString();
    }

    /**
     * Navigates to a nested object. <br>
     * <br>
     * This is equivalent to calling {@code obtainAny(name).asObject()}.
     *
     * @param name the property name
     *
     * @return the new ref, never {@code null}.
     *
     * @throws MissingElementException if the given name is not mapped to a value
     * @throws TypeMismatchException if the given name is mapped to a value that is not an object, e.g. a {@code null} literal
     */
    default ObjectRef obtainObject(String name)
    {
        return obtainAny(name).asObject();
    }

    /**
     * Navigates to a deeply nested object. <br>
     * <br>
     * Calling {@code obtainObject("a", "b", "c")} is equivalent to {@code obtainObject("a").obtainObject("b").obtainObject("c")}.
     *
     * @param firstLevel the property name on the first level
     * @param moreLevels property names to use on subsequent levels
     *
     * @return the new ref, never {@code null}.
     *
     * @throws MissingElementException if any of the names given is not mapped to a value, or to a {@code null} literal
     * @throws TypeMismatchException if any of the names given is mapped to a value that is not an object, e.g. a {@code null} literal
     */
    default ObjectRef obtainObject(String firstLevel, String... moreLevels)
    {
        return obtainAny(firstLevel, moreLevels).asObject();
    }

    /**
     * Navigates to a property that is an array of objects. <br>
     * <br>
     * This is equivalent to calling {@code obtainAny(name).asArrayOfObjects()}.
     *
     * @param name the property name
     *
     * @return the new ref, never {@code null}.
     *
     * @throws MissingElementException if the given name is not mapped to a value
     * @throws TypeMismatchException if the given name is mapped to a value that is not an array, e.g. a {@code null} literal
     */
    default ArrayRef<ObjectRef> obtainArrayOfObjects(String name)
    {
        return obtainAny(name).asArrayOfObjects();
    }

    /**
     * Navigates to an object array property of a nested object. <br>
     * <br>
     * Calling {@code obtainArrayOfObjects("a", "b", "c")} is equivalent to {@code obtainObject("a").obtainObject("b").obtainArrayOfObjects("c")}.
     *
     * @param firstLevel the property name on the first level
     * @param moreLevels property names to use on subsequent levels
     *
     * @return the new ref, never {@code null}.
     *
     * @throws MissingElementException if any of the names given is not mapped to a value, or to a {@code null} literal
     * @throws TypeMismatchException if the last name is mapped to a value that is not an array, or if any name except the last one is mapped to a value that is not an object, e.g. a {@code null} literal
     */
    default ArrayRef<ObjectRef> obtainArrayOfObjects(String firstLevel, String... moreLevels)
    {
        return obtainAny(firstLevel, moreLevels).asArrayOfObjects();
    }

    /**
     * Navigates to an array property. <br>
     * <br>
     * This is equivalent to calling {@code obtainAny(name).asArray(elementClass)}.
     *
     * @param <E> the type of array elements
     * @param elementClass the class to use for the array elements
     * @param name the property name
     *
     * @return the new ref, never {@code null}.
     *
     * @throws MissingElementException if the given name is not mapped to a value
     * @throws TypeMismatchException if the given name is mapped to a value that is not a boolean, e.g. a {@code null} literal
     */
    default <E extends JsonNode> ArrayRef<E> obtainArray(Class<E> elementClass, String name)
    {
        return obtainAny(name).asArray(elementClass);
    }

    /**
     * Navigates to an array property of a nested object. <br>
     * <br>
     * Calling {@code obtainArray(StringRef.class, "a", "b", "c")} is equivalent to {@code obtainObject("a").obtainObject("b").obtainArray(StringRef.class, "c")}.
     *
     * @param <E> the type of array elements
     * @param elementClass the class to use for the array elements
     * @param firstLevel the property name on the first level
     * @param moreLevels property names to use on subsequent levels
     *
     * @return the new ref, never {@code null}.
     *
     * @throws MissingElementException if any of the names given is not mapped to a value, or to a {@code null} literal
     * @throws TypeMismatchException if the last name is mapped to a value that is not an array, or if any name except the last one is mapped to a value that is not an object, e.g. a {@code null} literal
     */
    default <E extends JsonNode> ArrayRef<E> obtainArray(Class<E> elementClass, String firstLevel, String... moreLevels)
    {
        return obtainAny(firstLevel, moreLevels).asArray(elementClass);
    }

    default <V extends Value<T>, T, R> R obtainMapped(
        String name,
        Function<AnyRef, V> valueGetter,
        Function<T, R> mapper)
    {
        AnyRef anyRef = obtainAny(name);
        V value = valueGetter.apply(anyRef);
        T input = value.read();
        return mapper.apply(input);
    }
}
