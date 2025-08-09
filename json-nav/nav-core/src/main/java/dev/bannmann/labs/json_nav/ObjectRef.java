package dev.bannmann.labs.json_nav;

import static lombok.AccessLevel.PROTECTED;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import lombok.NoArgsConstructor;

import com.google.errorprone.annotations.Immutable;

/**
 * Represents a JSON object.
 */
@Immutable
@NoArgsConstructor(access = PROTECTED)
public abstract non-sealed class ObjectRef extends TypedRef
{
    private static final Predicate<AnyRef> EXCLUDE_NULL_REFS = Predicate.not(AnyRef::isNull);

    /**
     * Gets a JSON value that the given name maps to. <br>
     * <br>
     * Note that this method distinguishes between a non-existing mapping (empty {@code Optional}) and a mapping to a
     * JSON {@code null} literal ({@code Optional} containing a {@link NullRef}).
     *
     * @param name the name of the desired property.
     *
     * @return an {@code Optional} containing a ref to the JSON value that the given name maps to (which may be a {@link NullRef}), or an empty {@code Optional} if there is no such mapping.
     */
    public abstract Optional<AnyRef> tryGetAny(String name);

    /**
     * Gets a JSON Boolean that the given name maps to. <br>
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
    public final Optional<BooleanRef> tryGetBoolean(String name)
    {
        return tryGetTangibleValue(name, AnyRef::asBoolean);
    }

    private <T> Optional<T> tryGetTangibleValue(String name, Function<AnyRef, T> as)
    {
        return tryGetAny(name).filter(EXCLUDE_NULL_REFS)
            .map(as);
    }

    /**
     * Gets a JSON Number that the given name maps to. <br>
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
    public final Optional<NumberRef> tryGetNumber(String name)
    {
        return tryGetTangibleValue(name, AnyRef::asNumber);
    }

    /**
     * Gets a JSON String that the given name maps to. <br>
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
    public final Optional<StringRef> tryGetString(String name)
    {
        return tryGetTangibleValue(name, AnyRef::asString);
    }

    /**
     * Gets a JSON Object that the given name maps to. <br>
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
    public final Optional<ObjectRef> tryGetObject(String name)
    {
        return tryGetTangibleValue(name, AnyRef::asObject);
    }

    /**
     * Gets a JSON Array that the given name maps to. <br>
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
    public final Optional<ArrayRef<ObjectRef>> tryGetArrayOfObjects(String name)
    {
        return tryGetTangibleValue(name, AnyRef::asArrayOfObjects);
    }

    /**
     * Gets a JSON Array that the given name maps to. <br>
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
    public final <E extends TypedRef> Optional<ArrayRef<E>> tryGetArray(Class<E> elementClass, String name)
    {
        return tryGetTangibleValue(name, anyRef -> anyRef.asArray(elementClass));
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
    public final AnyRef obtainAny(String name)
    {
        return tryGetAny(name).orElseThrow(MissingElementException::new);
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
    public final AnyRef obtainAny(String firstLevel, String... moreLevels)
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
    public final BooleanRef obtainBoolean(String name)
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
     * @throws MissingElementException if any of the names given is not mapped to a value
     * @throws TypeMismatchException if the last name is mapped to a value that is not a boolean, or if any name except the last one is mapped to a value that is not an object, e.g. a {@code null} literal
     */
    public final BooleanRef obtainBoolean(String firstLevel, String... moreLevels)
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
    public final NumberRef obtainNumber(String name)
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
     * @throws MissingElementException if any of the names given is not mapped to a value
     * @throws TypeMismatchException if the last name is mapped to a value that is not a number, or if any name except the last one is mapped to a value that is not an object, e.g. a {@code null} literal
     */
    public final NumberRef obtainNumber(String firstLevel, String... moreLevels)
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
    public final StringRef obtainString(String name)
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
     * @throws MissingElementException if any of the names given is not mapped to a value
     * @throws TypeMismatchException if the last name is mapped to a value that is not a string, or if any name except the last one is mapped to a value that is not an object, e.g. a {@code null} literal
     */
    public final StringRef obtainString(String firstLevel, String... moreLevels)
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
    public final ObjectRef obtainObject(String name)
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
     * @throws MissingElementException if any of the names given is not mapped to a value
     * @throws TypeMismatchException if any of the names given is mapped to a value that is not an object, e.g. a {@code null} literal
     */
    public final ObjectRef obtainObject(String firstLevel, String... moreLevels)
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
    public final ArrayRef<ObjectRef> obtainArrayOfObjects(String name)
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
     * @throws MissingElementException if any of the names given is not mapped to a value
     * @throws TypeMismatchException if the last name is mapped to a value that is not an array, or if any name except the last one is mapped to a value that is not an object, e.g. a {@code null} literal
     */
    public final ArrayRef<ObjectRef> obtainArrayOfObjects(String firstLevel, String... moreLevels)
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
    public final <E extends TypedRef> ArrayRef<E> obtainArray(Class<E> elementClass, String name)
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
     * @throws MissingElementException if any of the names given is not mapped to a value
     * @throws TypeMismatchException if the last name is mapped to a value that is not an array, or if any name except the last one is mapped to a value that is not an object, e.g. a {@code null} literal
     */
    public final <E extends TypedRef> ArrayRef<E> obtainArray(
        Class<E> elementClass,
        String firstLevel,
        String... moreLevels)
    {
        return obtainAny(firstLevel, moreLevels).asArray(elementClass);
    }

    /**
     * Reads the value of a string property. <br>
     * <br>
     * This is equivalent to calling {@code obtainAny(name).readString()}.
     *
     * @param name the property name
     *
     * @return the string value, never {@code null}.
     *
     * @throws MissingElementException if the given name is not mapped to a value
     * @throws TypeMismatchException if the given name is mapped to a value that is not a string, e.g. a {@code null} literal
     */
    public final String readString(String name)
    {
        return obtainAny(name).readString();
    }

    /**
     * Reads the value of a string property of a nested object. <br>
     * <br>
     * Calling {@code readString("a", "b", "c")} is equivalent to {@code obtainObject("a").obtainObject("b").obtainAny("c").readString()}.
     *
     * @param firstLevel the property name on the first level
     * @param moreLevels property names to use on subsequent levels
     *
     * @return the string value, never {@code null}.
     *
     * @throws MissingElementException if any of the names given is not mapped to a value
     * @throws TypeMismatchException if any name except the last one is mapped to a value that is not an object, or if the last name is not mapped to a string, e.g. a {@code null} literal
     */
    public final String readString(String firstLevel, String... moreLevels)
    {
        return obtainAny(firstLevel, moreLevels).readString();
    }

    /**
     * Reads the value of a boolean property. <br>
     * <br>
     * This is equivalent to calling {@code obtainAny(name).readBoolean()}.
     *
     * @param name the property name
     *
     * @return the boolean value
     *
     * @throws MissingElementException if the given name is not mapped to a value
     * @throws TypeMismatchException if the given name is mapped to a value that is not a boolean, e.g. a {@code null} literal
     */
    public final boolean readBoolean(String name)
    {
        return obtainAny(name).readBoolean();
    }

    /**
     * Reads the value of a boolean property of a nested object. <br>
     * <br>
     * Calling {@code readString("a", "b", "c")} is equivalent to {@code obtainObject("a").obtainObject("b").obtainAny("c").readBoolean()}.
     *
     * @param firstLevel the property name on the first level
     * @param moreLevels property names to use on subsequent levels
     *
     * @return the boolean value
     *
     * @throws MissingElementException if any of the names given is not mapped to a value
     * @throws TypeMismatchException if any name except the last one is mapped to a value that is not an object, or if the last name is not mapped to a boolean, e.g. a {@code null} literal
     */
    public final boolean readBoolean(String firstLevel, String... moreLevels)
    {
        return obtainAny(firstLevel, moreLevels).readBoolean();
    }

    public final <V extends Value<T>, T, R> R readAnyMapped(
        String name,
        Function<AnyRef, V> valueGetter,
        Function<T, R> mapper)
    {
        var anyRef = obtainAny(name);
        V value = valueGetter.apply(anyRef);
        T input = value.read();
        return mapper.apply(input);
    }

    public final <R> R readStringMapped(String name, Function<String, R> mapper)
    {
        return readAnyMapped(name, AnyRef::asString, mapper);
    }

    public final <R> R map(Function<ObjectRef, R> function)
    {
        return function.apply(this);
    }

    /**
     * Gets the string that the given name maps to. <br>
     * <br>
     * This method does not distinguish between missing mappings and mappings to JSON {@code null} literals. If that
     * distinction is required, use {@link #tryGetAny(String)} instead.
     *
     * @param name the name of the desired property.
     *
     * @return an {@code Optional} containing the string that the given name maps to, or an empty {@code Optional}
     *
     * @throws TypeMismatchException if the given name is mapped to a value that is neither a string nor a {@code null} literal
     */
    public final Optional<String> tryReadString(String name)
    {
        return tryGetTangibleValue(name, AnyRef::asString).map(Value::read);
    }

    /**
     * Gets the boolean that the given name maps to. <br>
     * <br>
     * This method does not distinguish between missing mappings and mappings to JSON {@code null} literals. If that
     * distinction is required, use {@link #tryGetAny(String)} instead.
     *
     * @param name the name of the desired property.
     *
     * @return an {@code Optional} containing the boolean that the given name maps to, or an empty {@code Optional}
     *
     * @throws TypeMismatchException if the given name is mapped to a value that is neither a boolean nor a {@code null} literal
     */
    public final Optional<Boolean> tryReadBoolean(String name)
    {
        return tryGetTangibleValue(name, AnyRef::asBoolean).map(Value::read);
    }
}
