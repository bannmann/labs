package dev.bannmann.labs.json_nav.jackson;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.EqualsAndHashCode;

import com.fasterxml.jackson.databind.node.NumericNode;
import com.google.errorprone.annotations.Immutable;
import dev.bannmann.labs.annotations.SuppressWarningsRationale;
import dev.bannmann.labs.json_nav.AnyRef;
import dev.bannmann.labs.json_nav.NumberRef;
import dev.bannmann.labs.json_nav.TypeMismatchException;
import dev.bannmann.labs.json_nav.Value;

@Immutable
@EqualsAndHashCode(callSuper = false)
class JacksonNumber extends NumberRef implements AnyRef
{
    @SuppressWarnings("Immutable")
    @SuppressWarningsRationale("Jackson nodes are mutable, but we store a deep copy")
    private final NumericNode target;

    JacksonNumber(NumericNode target)
    {
        this.target = target.deepCopy();
    }

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
        if (!target.isIntegralNumber() && !target.canConvertToInt())
        {
            throw new TypeMismatchException();
        }
        return target::intValue;
    }

    @Override
    public Value<Long> intoLong()
    {
        if (!target.isIntegralNumber() && !target.canConvertToLong())
        {
            throw new TypeMismatchException();
        }
        return target::longValue;
    }

    @Override
    public Value<Short> intoShort()
    {
        if (!target.isIntegralNumber() || !target.canConvertToInt())
        {
            throw new TypeMismatchException();
        }
        if (target.intValue() < Short.MIN_VALUE || target.intValue() > Short.MAX_VALUE)
        {
            throw new TypeMismatchException("Value is out of range");
        }
        short result = target.shortValue();
        return () -> result;
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

    @Override
    public Value<BigInteger> intoBigInteger()
    {
        return target::bigIntegerValue;
    }

    @Override
    public String getRawJson()
    {
        return target.toString();
    }
}
