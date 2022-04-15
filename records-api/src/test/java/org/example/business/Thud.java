package org.example.business;

import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Value;

import com.github.mizool.core.Identifier;

@Value
@Builder
public class Thud
{
    Identifier<Corge> corgeId;

    Identifier<Quux> quuxId;

    String textData;

    boolean booleanData;

    OffsetDateTime timestamp;
}
