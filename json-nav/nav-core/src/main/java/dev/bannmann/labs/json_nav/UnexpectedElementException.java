package dev.bannmann.labs.json_nav;

import lombok.experimental.StandardException;

/**
 * Thrown when an array unexpectedly contains multiple elements.
 *
 * @see ArrayRef#onlyElement()
 */
@StandardException
public final class UnexpectedElementException extends NavigationException
{
}
