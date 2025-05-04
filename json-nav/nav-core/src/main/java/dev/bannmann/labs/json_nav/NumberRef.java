package dev.bannmann.labs.json_nav;

import java.math.BigDecimal;

import com.google.errorprone.annotations.Immutable;

@Immutable
public abstract non-sealed class NumberRef implements JsonNode
{
    public abstract Value<Integer> intoInteger();

    public abstract Value<Long> intoLong();

    public abstract Value<Double> intoDouble();

    public abstract Value<BigDecimal> intoBigDecimal();
}
