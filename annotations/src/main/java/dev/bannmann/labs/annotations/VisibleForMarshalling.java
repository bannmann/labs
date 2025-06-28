package dev.bannmann.labs.annotations;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;

/**
 * Annotates an element whose visibility was relaxed to make it work with marshalling, such as to/from JSON. <br>
 * <br>
 * Note: the name of this annotation intentionally mimics Guava's
 * {@link com.google.common.annotations.VisibleForTesting}.
 *
 * @see DisallowOutsideUse
 */
@Documented
@Target({ TYPE, FIELD, METHOD, CONSTRUCTOR })
@Implies(DisallowOutsideUse.class)
public @interface VisibleForMarshalling
{
}
