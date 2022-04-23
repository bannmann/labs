package com.github.bannmann.labs.integrationtest;

import lombok.Value;

import com.github.mizool.core.Identifiable;
import com.github.mizool.core.Identifier;

@Value
public class Origin implements Identifiable<Origin>
{
    Identifier<Origin> id;
}
