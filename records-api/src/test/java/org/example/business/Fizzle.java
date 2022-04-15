package org.example.business;

import lombok.Builder;
import lombok.Value;

import com.github.mizool.core.Identifiable;
import com.github.mizool.core.Identifier;

@Value
@Builder
public class Fizzle implements Identifiable<Fizzle>
{
    Identifier<Fizzle> id;

    String textData;

    boolean booleanData;
}
