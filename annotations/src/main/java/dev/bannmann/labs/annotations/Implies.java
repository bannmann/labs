package dev.bannmann.labs.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;

import java.lang.annotation.Annotation;
import java.lang.annotation.Target;

/**
 * Declares that elements annotated with one annotation should be treated as if another annotation was also present.
 */
@Target(ANNOTATION_TYPE)
public @interface Implies
{
    Class<? extends Annotation>[] value();
}
