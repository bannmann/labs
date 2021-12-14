package org.example.business;

import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Value;

import com.github.mizool.core.Identifiable;
import com.github.mizool.core.Identifier;

@Value
@Builder
public class Bar implements Identifiable<Bar>
{
    Identifier<Bar> id;

    String textData;

    boolean booleanData;

    OffsetDateTime timestamp;
}
