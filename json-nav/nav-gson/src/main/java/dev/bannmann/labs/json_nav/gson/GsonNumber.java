package dev.bannmann.labs.json_nav.gson;

import java.math.BigDecimal;
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
        return intoValue(target::getAsInt);
    }

    private <T> Value<T> intoValue(Supplier<T> supplier)
    {
        try
        {
            T value = supplier.get();
            return () -> value;
        }
        catch (NumberFormatException e)
        {
            throw new TypeMismatchException(e);
        }
    }

    @Override
    public Value<Long> intoLong()
    {
        return intoValue(target::getAsLong);
    }

    @Override
    public Value<Double> intoDouble()
    {
        return intoValue(target::getAsDouble);
    }

    @Override
    public Value<BigDecimal> intoBigDecimal()
    {
        return intoValue(target::getAsBigDecimal);
    }

    @Override
    public String getRawJson()
    {
        return target.toString();
    }
}
