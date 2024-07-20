package dev.bannmann.labs.annotations;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.MODULE;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Documents the author's rationale for using {@link SuppressWarnings}. This is superior to using inline comments or
 * Javadoc comments because it is more obviously linked to the other annotation.
 */
@Target({ TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE, MODULE })
@Retention(SOURCE)
@Repeatable(SuppressWarningsRationales.class)
public @interface SuppressWarningsRationale
{
    /**
     * The name of the warning which is suppressed. Could be left empty if obvious.
     *
     * @return the name of the warning which is suppressed
     */
    String name() default "";

    /**
     * A string explaining the reason for suppressing the warning.
     *
     * @return the rationale
     */
    String value();
}
