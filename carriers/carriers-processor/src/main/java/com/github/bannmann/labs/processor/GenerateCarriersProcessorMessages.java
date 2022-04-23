package com.github.bannmann.labs.processor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import io.toolisticon.aptk.tools.corematcher.ValidationMessage;

/**
 * Messages used by annotation processors.
 */
@Getter
@RequiredArgsConstructor
enum GenerateCarriersProcessorMessages implements ValidationMessage
{
    ERROR_COULD_NOT_CREATE_CLASS("GenerateCarriers_ERROR_001", "Could not create class ${0}: ${1}"),
    ERROR_NO_IDENTIFIER_FIELDS("GenerateCarriers_ERROR_002",
        "Annotated class ${0} does not have any Identifier fields referencing other classes"),
    ERROR_ILLEGAL_FIELD_NAME("GenerateCarriers_ERROR_003",
        "Illegal field name '${1}' in class ${0}: must end in 'Id' or 'Identifier'");

    private final String code;
    private final String message;
}
