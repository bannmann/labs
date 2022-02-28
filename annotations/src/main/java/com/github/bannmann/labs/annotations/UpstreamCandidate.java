package com.github.bannmann.labs.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Target;

/**
 * Marks a class or method that could probably be moved to the given upstream library.
 */
@Target({ TYPE, METHOD })
public @interface UpstreamCandidate
{
    String value();
}
