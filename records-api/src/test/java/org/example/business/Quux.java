package org.example.business;

import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Value;

import com.github.mizool.core.Identifier;

@Value
@Builder
public class Quux
{
    Identifier<Quux> id;

    String textData;

    boolean booleanData;

    OffsetDateTime created;

    OffsetDateTime updated;
}
