package com.github.bannmann.labs.processor;

import java.util.Set;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import lombok.Builder;
import lombok.Value;

/**
 * Represents a field. Needed because {@link VariableElement} does not implement {@link Object#equals(Object)} and
 * {@link Object#hashCode()} and thus cannot be used in a {@link Set}.
 */
@Value
@Builder
class IdReference
{
    String idFieldName;
    TypeMirror pojoType;
    String pojoFieldName;

    public String getIdGetterName()
    {
        return "get" + Names.capitalize(idFieldName);
    }
}
