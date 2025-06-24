package dev.bannmann.labs.json_nav.jakarta;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Supplier;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import com.google.errorprone.annotations.Immutable;
import dev.bannmann.labs.annotations.SuppressWarningsRationale;
import dev.bannmann.labs.json_nav.AnyRef;
import dev.bannmann.labs.json_nav.NumberRef;
import dev.bannmann.labs.json_nav.TypeMismatchException;
import dev.bannmann.labs.json_nav.Value;
import jakarta.json.JsonNumber;

@Immutable
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
class JsonpNumber extends NumberRef implements AnyRef
{
    @SuppressWarnings("Immutable")
    @SuppressWarningsRationale("jakarta.json values *are* immutable")
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
        return wrap(target::intValueExact);
    }

    /**
     * Retrieves and checks the value immediately instead of delaying that until the lambda is called.
     */
    private <T> Value<T> wrap(Supplier<T> supplier)
    {
        try
        {
            var result = supplier.get();
            return () -> result;
        }
        catch (ArithmeticException e)
        {
            throw new TypeMismatchException(e);
        }
    }

    @Override
    public Value<Long> intoLong()
    {
        return wrap(target::longValueExact);
    }

    @Override
    public Value<Short> intoShort()
    {
        return wrap(() -> {
            int integer = target.intValueExact();
            if (integer < Short.MIN_VALUE || integer > Short.MAX_VALUE)
            {
                throw new TypeMismatchException("Value is out of range");
            }
            return (short) integer;
        });
    }

    @Override
    public Value<Double> intoDouble()
    {
        return wrap(() -> {
            double result = target.doubleValue();
            if (target.bigDecimalValue()
                    .compareTo(BigDecimal.valueOf(result)) != 0)
            {
                throw new TypeMismatchException();
            }
            return result;
        });
    }

    @Override
    public Value<BigDecimal> intoBigDecimal()
    {
        return wrap(target::bigDecimalValue);
    }

    @Override
    public Value<BigInteger> intoBigInteger()
    {
        return wrap(() -> {
            BigDecimal bigDecimal = target.bigDecimalValue();
            if (bigDecimal.stripTrailingZeros()
                    .scale() > 0)
            {
                throw new TypeMismatchException();
            }
            return bigDecimal.toBigInteger();
        });
    }

    @Override
    public String getRawJson()
    {
        return target.toString();
    }
}
