package com.github.bannmann.labs.core;

import java.util.Optional;

import lombok.experimental.UtilityClass;

import com.github.bannmann.labs.annotations.UpstreamCandidate;

@UtilityClass
public class ObjectExtras
{
    @UpstreamCandidate("Mizool")
    public <T> Optional<T> tryCast(Object object, Class<T> castTo)
    {
        return Optional.ofNullable(object)
            .filter(castTo::isInstance)
            .map(castTo::cast);
    }
}
