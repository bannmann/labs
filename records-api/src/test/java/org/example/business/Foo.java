package org.example.business;

import lombok.Builder;
import lombok.Value;

import com.github.mizool.core.Identifiable;
import com.github.mizool.core.Identifier;

@Value
@Builder
public class Foo implements Identifiable<Foo>
{
    Identifier<Foo> id;

    String textData;

    boolean booleanData;

    int version;
}
