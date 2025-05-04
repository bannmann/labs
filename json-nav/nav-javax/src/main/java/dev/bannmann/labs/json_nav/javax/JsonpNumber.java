package dev.bannmann.labs.json_nav.javax;

import java.math.BigDecimal;

import javax.json.JsonNumber;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import com.google.errorprone.annotations.Immutable;
import dev.bannmann.labs.annotations.SuppressWarningsRationale;
import dev.bannmann.labs.json_nav.AnyRef;
import dev.bannmann.labs.json_nav.NumberRef;
import dev.bannmann.labs.json_nav.Value;

@Immutable
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
class JsonpNumber extends NumberRef implements AnyRef
{
    @SuppressWarnings("Immutable")
    @SuppressWarningsRationale("javax.json values *are* immutable")
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

    @Override
    public String getRawJson()
    {
        return target.toString();
    }
}
