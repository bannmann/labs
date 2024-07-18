package dev.bannmann.labs.core;

import java.util.Optional;

import lombok.experimental.UtilityClass;

import org.jspecify.annotations.Nullable;

import dev.bannmann.labs.annotations.UpstreamCandidate;

@UtilityClass
public class ObjectExtras
{
    @UpstreamCandidate("Mizool")
    public <T> Optional<T> tryCast(@Nullable Object object, Class<T> castTo)
    {
        return Optional.ofNullable(object)
            .filter(castTo::isInstance)
            .map(castTo::cast);
    }
}
