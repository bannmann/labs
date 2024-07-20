package dev.bannmann.labs.annotations;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Target;

/**
 * Annotates an element that was made public only to make it work with marshalling, such as to/from JSON.<br>
 * <br>
 * Note: the name of this annotation intentionally mimics Guava's
 * {@link com.google.common.annotations.VisibleForTesting}.
 */
@Target({ TYPE, CONSTRUCTOR })
public @interface VisibleForMarshalling
{
}
