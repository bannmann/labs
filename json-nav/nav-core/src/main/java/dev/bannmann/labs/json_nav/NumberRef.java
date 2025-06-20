package dev.bannmann.labs.json_nav;

import java.math.BigDecimal;

import com.google.errorprone.annotations.Immutable;

/**
 * Represents a JSON number.
 */
@Immutable
public abstract non-sealed class NumberRef extends TypedRef
{
    /**
     * @see #readInteger()
     */
    public abstract Value<Integer> intoInteger();

    /**
     * @see #readLong()
     */
    public abstract Value<Long> intoLong();

    /**
     * @see #readDouble()
     */
    public abstract Value<Double> intoDouble();

    /**
     * @see #readBigDecimal()
     */
    public abstract Value<BigDecimal> intoBigDecimal();

    /**
     * @see #intoInteger()
     */
    public int readInteger()
    {
        return intoInteger().read();
    }

    /**
     * @see #intoLong()
     */
    public long readLong()
    {
        return intoLong().read();
    }

    /**
     * @see #intoDouble()
     */
    public double readDouble()
    {
        return intoDouble().read();
    }

    /**
     * @see #intoBigDecimal()
     */
    public BigDecimal readBigDecimal()
    {
        return intoBigDecimal().read();
    }
}
