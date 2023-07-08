package com.github.bannmann.labs.records_api;

import lombok.Getter;
import lombok.NonNull;

import com.github.bannmann.labs.annotations.UpstreamCandidate;
import com.github.mizool.core.exception.AbstractUnprocessableEntityException;

@UpstreamCandidate("Mizool")
@Getter
public class EntityReferenceException extends AbstractUnprocessableEntityException
{
    private final String fieldName;
    private final String recordId;

    public EntityReferenceException(@NonNull String fieldName)
    {
        this(fieldName, (Throwable) null);
    }

    public EntityReferenceException(@NonNull String fieldName, Throwable cause)
    {
        super(String.format("Invalid entity reference in field %s", fieldName), cause);
        this.fieldName = fieldName;
        recordId = null;
    }

    public EntityReferenceException(@NonNull String fieldName, @NonNull String recordId)
    {
        this(fieldName, recordId, null);
    }

    public EntityReferenceException(@NonNull String fieldName, @NonNull String recordId, Throwable cause)
    {
        super(String.format("Invalid entity reference in field %s of '%s'", fieldName, recordId), cause);
        this.fieldName = fieldName;
        this.recordId = recordId;
    }
}
