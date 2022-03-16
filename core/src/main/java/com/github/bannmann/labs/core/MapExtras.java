package com.github.bannmann.labs.core;

import java.util.Map;
import java.util.Optional;

import lombok.experimental.UtilityClass;

import com.github.bannmann.labs.annotations.UpstreamCandidate;

@UtilityClass
public class MapExtras
{
    @UpstreamCandidate("Mizool")
    public <K, V> Optional<V> tryGet(Map<K, V> map, K key)
    {
        return Optional.ofNullable(map.get(key));
    }
}
