package dev.bannmann.labs.integrationtest;

import lombok.Value;

import com.github.mizool.core.Identifiable;
import com.github.mizool.core.Identifier;
import dev.bannmann.labs.api.GenerateCarriers;

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
