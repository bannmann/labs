package dev.bannmann.labs.records_api;

import lombok.Getter;
import lombok.NonNull;

import com.github.mizool.core.exception.AbstractUnprocessableEntityException;

@Getter
public class FieldExclusionException extends AbstractUnprocessableEntityException
{
    private final String fieldName;

    public FieldExclusionException(@NonNull String fieldName)
    {
        super(String.format("Field %s must not be specified for this operation", fieldName));
        this.fieldName = fieldName;
    }
}
