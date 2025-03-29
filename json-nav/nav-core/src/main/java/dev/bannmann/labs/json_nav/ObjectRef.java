package dev.bannmann.labs.json_nav;

import java.util.Optional;
import java.util.function.Function;

import org.jspecify.annotations.NullMarked;

@NullMarked
public non-sealed interface ObjectRef extends JsonNode
{
    Optional<AnyRef> tryGet(String name);

    default Optional<ObjectRef> tryGetObject(String name)
    {
        return tryGet(name).map(AnyRef::asObject);
    }

    /**
     * Navigates to the given property name.
     *
     * @param name the property name
     *
     * @return the new ref, never {@code null}.
     *
     * @throws MissingElementException if the given name is not mapped to a value
     */
    AnyRef obtain(String name);

    AnyRef obtain(String firstLevel, String... moreLevels);

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
     * @throws TypeMismatchException if the given name is mapped to a value that is not an object
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
