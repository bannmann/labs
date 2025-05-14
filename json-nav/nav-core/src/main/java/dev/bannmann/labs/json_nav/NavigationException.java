package dev.bannmann.labs.json_nav;

import lombok.experimental.StandardException;

@StandardException
public abstract sealed class NavigationException extends RuntimeException
    permits MissingElementException, TypeMismatchException, UnexpectedElementException
{
}
