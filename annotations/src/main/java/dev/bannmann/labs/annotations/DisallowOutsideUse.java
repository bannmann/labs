package dev.bannmann.labs.annotations;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;

/**
 * Annotates an element that production code should treat as if it had private or default visibility. Concrete
 * annotations that {@linkplain Implies imply} {@code DisallowOutsideUse} could pose additional restrictions.
 *
 * @see VisibleForProxies
 * @see VisibleForMarshalling
 * @see VisibleForServiceLoader
 */
@Documented
@Target({ TYPE, METHOD, CONSTRUCTOR, FIELD })
public @interface DisallowOutsideUse
{
}
