package dev.bannmann.labs.annotations;

import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated field or method can only be used within an action of a
 * {@link com.github.mizool.core.concurrent.Synchronizer} instance. <br>
 * <br>
 * This annotation serves to document the author's intention in the absence of tooling-oriented annotations like
 * {@code GuardedBy} which would lead to false positives.
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(CLASS)
@Documented
public @interface GuardedBySynchronizer
{
}
