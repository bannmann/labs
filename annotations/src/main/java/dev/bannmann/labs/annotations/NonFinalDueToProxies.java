package dev.bannmann.labs.annotations;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;

/**
 * Annotates a class that is meant to be final, but cannot due to proxies.
 *
 * @see DisallowSubclasses
 */
@Documented
@Target(TYPE)
@Implies(DisallowSubclasses.class)
public @interface NonFinalDueToProxies
{
}
