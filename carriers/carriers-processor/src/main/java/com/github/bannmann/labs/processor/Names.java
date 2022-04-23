package com.github.bannmann.labs.processor;

import java.util.Locale;

import lombok.experimental.UtilityClass;

@UtilityClass
class Names
{
    public static String capitalize(String name)
    {
        return name.substring(0, 1)
            .toUpperCase(Locale.ROOT) + name.substring(1);
    }

    public static String uncapitalize(String name)
    {
        return name.substring(0, 1)
            .toLowerCase(Locale.ROOT) + name.substring(1);
    }
}
