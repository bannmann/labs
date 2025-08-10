package dev.bannmann.labs.annotations;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;

/**
 * Annotates an element that was made public only to make it work with the
 * {@linkplain java.util.ServiceLoader service loader} mechanism. That mechanism requires classes to be {@code public}
 * and have a {@code public} no-arg constructor. The class or interface that the individual services implement/inherit
 * also needs to be {@code public}.
 *
 * <p>Note: the name of this annotation intentionally mimics Guava's
 * {@code com.google.common.annotations.VisibleForTesting}.
 *
 * @see DisallowOutsideUse
 */
@Documented
@Target({TYPE, CONSTRUCTOR})
@Implies(DisallowOutsideUse.class)
public @interface VisibleForServiceLoader
{
}
