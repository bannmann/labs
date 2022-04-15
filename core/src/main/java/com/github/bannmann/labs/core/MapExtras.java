package com.github.bannmann.labs.core;

import java.util.Map;
import java.util.Optional;

import lombok.experimental.UtilityClass;

import com.github.bannmann.labs.annotations.UpstreamCandidate;

@UtilityClass
public class MapExtras
{
    /**
     * Gets the value to which the given key is mapped, if existing. <br>
     * <br>
     * Unlike {@link Map#get(Object)}, with this method the compiler verifies type of the key argument. Also, as it
     * returns an {@link Optional}, calling code can use chained method calls instead of separate {@code if} blocks.
     *
     * @return a present {@link Optional} if the given map contains a non-{@code null} value for the given {@code key}.
     */
    @UpstreamCandidate("Mizool")
    public <K, V> Optional<V> tryGet(Map<K, V> map, K key)
    {
        return Optional.ofNullable(map.get(key));
    }

    /**
     * Removes a mapping for the given key, if one exists.
     *
     * @return a present {@link Optional} of the value associated with the given {@code key}, if one existed.
     */
    @UpstreamCandidate("Mizool")
    public <K, V> Optional<V> tryRemove(Map<K, V> map, K key)
    {
        return Optional.ofNullable(map.remove(key));
    }
}
