package dev.bannmann.labs.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.RECORD_COMPONENT;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Target;

/**
 * Like {@link SuppressWarnings}, but with class retention so that we can use it in unit tests.
 */
@Target({ TYPE, RECORD_COMPONENT, PARAMETER, FIELD })
public @interface SuppressCodeRules
{
    String[] value();
}
