package dev.bannmann.labs.json_nav;

import static lombok.AccessLevel.PACKAGE;

import lombok.experimental.StandardException;

/**
 * Base class for all exceptions thrown by JsonNav.
 */
@StandardException(access = PACKAGE)
public abstract sealed class NavigationException extends RuntimeException
    permits MissingElementException, TypeMismatchException, UnexpectedElementException
{
}
