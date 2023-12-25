package dev.bannmann.labs.core;

import java.util.Optional;

import lombok.experimental.UtilityClass;

import dev.bannmann.labs.annotations.UpstreamCandidate;

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
