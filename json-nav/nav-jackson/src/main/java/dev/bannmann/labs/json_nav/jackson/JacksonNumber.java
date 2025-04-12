package dev.bannmann.labs.json_nav.jackson;

import java.math.BigDecimal;

import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.databind.node.NumericNode;
import dev.bannmann.labs.json_nav.AnyRef;
import dev.bannmann.labs.json_nav.NumberRef;
import dev.bannmann.labs.json_nav.TypeMismatchException;
import dev.bannmann.labs.json_nav.Value;

@RequiredArgsConstructor
class JacksonNumber implements NumberRef, AnyRef
{
    private final NumericNode target;

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
        if (!target.isIntegralNumber() || !target.canConvertToInt())
        {
            throw new TypeMismatchException();
        }
        return target::intValue;
    }

    @Override
    public Value<Long> intoLong()
    {
        if (!target.isIntegralNumber() || !target.canConvertToLong())
        {
            throw new TypeMismatchException();
        }
        return target::longValue;
    }

    @Override
    public Value<Double> intoDouble()
    {
        /*
         * doubleValue says:
         * "For integer values, conversion is done using coercion; this may result in overflows with BigInteger values."
         *
         * Let's play it safe by rejecting values that would require a BigInteger (i.e. not fit into a long).
         */
        if (target.isIntegralNumber() && !target.canConvertToLong())
        {
            throw new TypeMismatchException();
        }
        return target::doubleValue;
    }

    @Override
    public Value<BigDecimal> intoBigDecimal()
    {
        return target::decimalValue;
    }
}
