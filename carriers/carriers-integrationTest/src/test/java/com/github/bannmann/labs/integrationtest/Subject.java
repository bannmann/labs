package com.github.bannmann.labs.integrationtest;

import lombok.Value;

import com.github.bannmann.labs.api.GenerateCarriers;
import com.github.mizool.core.Identifiable;
import com.github.mizool.core.Identifier;

/**
 * @see SubjectCarryingOrigin
 * @see SubjectCarryingDestination
 * @see SubjectCarryingOriginAndDestination
 */
@Value
@GenerateCarriers
public class Subject implements Identifiable<Subject>
{
    Identifier<Subject> id;

    Identifier<Origin> originId;

    Identifier<Destination> destinationId;

    String label;
}
