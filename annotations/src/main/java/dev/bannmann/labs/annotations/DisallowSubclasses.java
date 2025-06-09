package dev.bannmann.labs.annotations;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Target;

/**
 * Annotates a class that should not have subclasses in production code.
 *
 * @see NonFinalDueToInterceptors
 * @see NonFinalDueToProxies
 */
@Target(TYPE)
public @interface DisallowSubclasses
{
}
