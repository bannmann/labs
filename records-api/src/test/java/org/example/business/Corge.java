package org.example.business;

import lombok.Builder;
import lombok.Value;

import com.github.mizool.core.Identifiable;
import com.github.mizool.core.Identifier;

@Value
@Builder
public class Corge implements Identifiable<Corge>
{
    Identifier<Corge> id;

    String textData;

    boolean booleanData;

    String tag;
}
