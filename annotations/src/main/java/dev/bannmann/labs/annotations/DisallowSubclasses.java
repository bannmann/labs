package dev.bannmann.labs.annotations;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;

/**
 * Annotates a class that should not have subclasses in production code.
 *
 * @see NonFinalDueToInterceptors
 * @see NonFinalDueToProxies
 */
@Documented
@Target(TYPE)
public @interface DisallowSubclasses
{
}
