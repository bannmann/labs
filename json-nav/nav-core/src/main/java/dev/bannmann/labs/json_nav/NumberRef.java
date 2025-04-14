package dev.bannmann.labs.json_nav;

import java.math.BigDecimal;

import com.google.errorprone.annotations.Immutable;

@Immutable
public non-sealed interface NumberRef extends JsonNode
{
    Value<Integer> intoInteger();

    Value<Long> intoLong();

    Value<Double> intoDouble();

    Value<BigDecimal> intoBigDecimal();
}
