package com.github.bannmann.labs.processor;

import javax.lang.model.element.Element;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import io.toolisticon.aptk.tools.MessagerUtils;
import io.toolisticon.aptk.tools.corematcher.ValidationMessage;

@RequiredArgsConstructor
public class ProcessingErrorException extends RuntimeException
{
    @Getter
    private final Runnable messageEvoker;

    public ProcessingErrorException(Element element, ValidationMessage message, Object... args)
    {
        this(null, element, message, args);
    }

    public ProcessingErrorException(Throwable cause, Element element, ValidationMessage message, Object... args)
    {
        super(cause);
        messageEvoker = () -> MessagerUtils.error(element, message, args);
    }
}
