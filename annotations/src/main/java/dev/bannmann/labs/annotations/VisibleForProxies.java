package dev.bannmann.labs.annotations;

import static java.lang.annotation.ElementType.CONSTRUCTOR;

import java.lang.annotation.Target;

/**
 * Annotates a constructor that exists only to make the class proxyable for CDI. Any non-private constructor is
 * sufficient, e.g. package-private. <br>
 * <br>
 * Note: the name of this annotation intentionally mimics Guava's
 * {@link com.google.common.annotations.VisibleForTesting}.
 */
@Target({ CONSTRUCTOR })
public @interface VisibleForProxies
{
}
