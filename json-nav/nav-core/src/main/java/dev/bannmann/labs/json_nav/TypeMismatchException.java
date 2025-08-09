package dev.bannmann.labs.json_nav;

import lombok.experimental.StandardException;

/**
 * Thrown when a value has the wrong type. For example, when looking for an object, but the given property is
 * {@code null} instead.
 *
 * <p>If a property does not exist, {@link MissingElementException} is thrown instead.
 */
@StandardException
public final class TypeMismatchException extends NavigationException
{
}
