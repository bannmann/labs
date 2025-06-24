package dev.bannmann.labs.json_nav.gson;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Supplier;

import lombok.EqualsAndHashCode;

import com.google.errorprone.annotations.Immutable;
import com.google.gson.JsonPrimitive;
import dev.bannmann.labs.annotations.SuppressWarningsRationale;
import dev.bannmann.labs.json_nav.AnyRef;
import dev.bannmann.labs.json_nav.NumberRef;
import dev.bannmann.labs.json_nav.TypeMismatchException;
import dev.bannmann.labs.json_nav.Value;

@Immutable
@EqualsAndHashCode(callSuper = false)
class GsonNumber extends NumberRef implements AnyRef
{
    @SuppressWarnings("Immutable")
    @SuppressWarningsRationale("Gson elements are mutable, but we store a deep copy")
    private final JsonPrimitive target;

    GsonNumber(JsonPrimitive target)
    {
        if (!target.isNumber())
        {
            throw new IllegalArgumentException();
        }
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
        return wrap(() -> {
            var result = target.getAsInt();
            if (target.getAsBigDecimal()
                    .compareTo(BigDecimal.valueOf(result)) != 0)
            {
                throw new TypeMismatchException();
            }
            return result;
        });
    }

    /**
     * Retrieves and checks the value immediately instead of delaying that until the lambda is called.
     */
    private <T> Value<T> wrap(Supplier<T> supplier)
    {
        try
        {
            T result = supplier.get();
            return () -> result;
        }
        catch (NumberFormatException e)
        {
            throw new TypeMismatchException(e);
        }
    }

    @Override
    public Value<Long> intoLong()
    {
        return wrap(() -> {
            var result = target.getAsLong();
            if (target.getAsBigDecimal()
                    .compareTo(BigDecimal.valueOf(result)) != 0)
            {
                throw new TypeMismatchException();
            }
            return result;
        });
    }

    @Override
    public Value<Short> intoShort()
    {
        return wrap(() -> {
            var result = target.getAsShort();
            if (target.getAsBigDecimal()
                    .compareTo(BigDecimal.valueOf(result)) != 0)
            {
                throw new TypeMismatchException();
            }
            return result;
        });
    }

    @Override
    public Value<Double> intoDouble()
    {
        return wrap(() -> {
            double result = target.getAsDouble();
            if (target.getAsBigDecimal()
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
        return wrap(target::getAsBigDecimal);
    }

    @Override
    public Value<BigInteger> intoBigInteger()
    {
        return wrap(target::getAsBigInteger);
    }

    @Override
    public String getRawJson()
    {
        return target.toString();
    }
}
