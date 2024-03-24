package org.example.business;

import lombok.Builder;
import lombok.Value;

import com.github.mizool.core.Identifiable;

@Value
@Builder
public class Splat implements Identifiable<Splat>
{
    Integer sequence;
    String textData;
}
