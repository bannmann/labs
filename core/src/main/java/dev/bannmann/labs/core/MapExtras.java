package dev.bannmann.labs.core;

import java.util.Map;
import java.util.Optional;

import lombok.experimental.UtilityClass;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import dev.bannmann.labs.annotations.UpstreamCandidate;

@UtilityClass
public class MapExtras
{
    /**
     * Gets the value to which the given key is mapped, if existing.
     *
     * <p>Unlike {@link Map#get(Object)}, with this method the compiler verifies the type of the key argument. Also, as
     * it returns an {@link Optional}, calling code can use chained method calls instead of separate {@code if} blocks.
     *
     * @return a present {@link Optional} if the given map contains a non-{@code null} value for the given {@code key}.
     */
    @UpstreamCandidate("Mizool")
    public <K extends @Nullable Object, V extends @Nullable Object> Optional<@NonNull V> tryGet(Map<K, V> map, K key)
    {
        return Optional.ofNullable(map.get(key));
    }

    /**
     * Removes a mapping for the given key, if one exists.
     *
     * @return a present {@link Optional} of the value associated with the given {@code key}, if one existed.
     */
    @UpstreamCandidate("Mizool")
    public <K extends @Nullable Object, V extends @Nullable Object> Optional<@NonNull V> tryRemove(Map<K, V> map, K key)
    {
        return Optional.ofNullable(map.remove(key));
    }

    /**
     * Gets the value to which the given key is mapped or throws an exception.
     *
     * <p>Unlike {@link Map#get(Object)}, with this method the compiler verifies type of the key argument.
     *
     * @return the non-null value for the given key
     *
     * @throws IllegalArgumentException if the given map does not contain a non-{@code null} value for the given {@code key}.
     */
    @UpstreamCandidate("Mizool")
    public <K extends @Nullable Object, V extends @Nullable Object> @NonNull V obtain(Map<K, V> map, K key)
    {
        V result = map.get(key);
        if (result == null)
        {
            throw new IllegalArgumentException("Key %s not found in map".formatted(key));
        }
        return result;
    }
}
