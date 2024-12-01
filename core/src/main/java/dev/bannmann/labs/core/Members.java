package dev.bannmann.labs.core;

import lombok.experimental.UtilityClass;

import org.jspecify.annotations.Nullable;

@UtilityClass
public class Members
{
    public static <T> T getOrDefault(@Nullable T configuredValue, T defaultValue)
    {
        return configuredValue != null
            ? configuredValue
            : defaultValue;
    }
}
