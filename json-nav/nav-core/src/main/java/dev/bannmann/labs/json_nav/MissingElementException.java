package dev.bannmann.labs.json_nav;

import lombok.experimental.StandardException;

/**
 * Thrown when an object does not have a mapping for the given name, or when attempting to get elements from an empty
 * array.
 *
 * <p>If the name is mapped, but its value is of the wrong type (e.g. a string instead of an object),
 * {@link TypeMismatchException} is thrown instead.
 */
@StandardException
public final class MissingElementException extends NavigationException
{
}
