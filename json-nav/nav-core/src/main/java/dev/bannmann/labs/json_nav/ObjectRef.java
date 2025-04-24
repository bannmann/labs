package dev.bannmann.labs.json_nav;

import static java.util.function.Predicate.not;

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
    Optional<AnyRef> tryGet(String name);

    /**
     * Gets a JSON Object that the given name maps to.<br>
     * <br>
     * This method does not distinguish between missing mappings and mappings to JSON {@code null} literals.
     *
     * @param name the name of the desired property.
     *
     * @return an {@code Optional} containing a ref to the JSON Object that the given name maps to, or an empty {@code Optional}
     *
     * @throws TypeMismatchException if the given name is mapped to a value that is neither an object nor a {@code null} literal
     */
    default Optional<ObjectRef> tryGetObject(String name)
    {
        return tryGet(name).filter(not(AnyRef::isNull))
            .map(AnyRef::asObject);
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
    default AnyRef obtain(String name)
    {
        return tryGet(name).orElseThrow(MissingElementException::new);
    }

    /**
     * Navigates to a property of a nested object.<br>
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
     */
    default AnyRef obtain(String firstLevel, String... moreLevels)
    {
        if (moreLevels.length == 0)
        {
            return obtain(firstLevel);
        }

        ObjectRef currentObject = obtainObject(firstLevel);
        for (int i = 0; i < moreLevels.length - 1; i++)
        {
            currentObject = currentObject.obtainObject(moreLevels[i]);
        }

        return currentObject.obtain(moreLevels[moreLevels.length - 1]);
    }

    /**
     * Navigates to the object to which the given name is mapped.<br>
     * <br>
     * This is equivalent to calling {@code obtain(name).asObject()}.
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
        return obtain(name).asObject();
    }

    default <V extends Value<T>, T, R> R obtainMapped(
        String name,
        Function<AnyRef, V> valueGetter,
        Function<T, R> mapper)
    {
        AnyRef anyRef = obtain(name);
        V value = valueGetter.apply(anyRef);
        T input = value.read();
        return mapper.apply(input);
    }
}
