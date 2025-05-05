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
 * Holds a comment that does not belong in javadoc. <br>
 * <br>
 * Unlike a single line comment, this annotation can be placed outside class members without IDEs or linters complaining
 * about (or even removing) the "dangling" comment.
 */
@Target({ TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE, MODULE })
@Retention(SOURCE)
@Repeatable(ImplementationNotes.class)
public @interface ImplementationNote
{
    String value();
}
