package dev.bannmann.labs.json_nav.jakarta;

import java.math.BigDecimal;

import lombok.RequiredArgsConstructor;

import dev.bannmann.labs.json_nav.AnyRef;
import dev.bannmann.labs.json_nav.NumberRef;
import dev.bannmann.labs.json_nav.Value;
import jakarta.json.JsonNumber;

@RequiredArgsConstructor
class JsonpNumber implements NumberRef, AnyRef
{
    private final JsonNumber target;

    @Override
    public boolean isNumber()
    {
        return true;
    }

    @Override
    public NumberRef asNumber()
    {
        return this;
    }

    @Override
    public Value<Integer> intoInteger()
    {
        return target::intValueExact;
    }

    @Override
    public Value<Long> intoLong()
    {
        return target::longValueExact;
    }

    @Override
    public Value<Double> intoDouble()
    {
        return target::doubleValue;
    }

    @Override
    public Value<BigDecimal> intoBigDecimal()
    {
        return target::bigDecimalValue;
    }
}
