package com.github.bannmann.labs.core;

import java.util.Optional;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import com.github.bannmann.labs.annotations.UpstreamCandidate;

@UtilityClass
@Slf4j
public class ClassExtras
{
    @UpstreamCandidate("Mizool")
    public Optional<Class<?>> tryResolve(String className)
    {
        try
        {
            return Optional.of(Class.forName(className));
        }
        catch (ClassNotFoundException e)
        {
            log.debug("Could not resolve class {}", className, e);
            return Optional.empty();
        }
    }
}
