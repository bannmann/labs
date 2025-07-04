package dev.bannmann.labs.json_nav;

import static lombok.AccessLevel.PROTECTED;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.NoArgsConstructor;

import com.google.errorprone.annotations.Immutable;

/**
 * Represents a JSON number.
 */
@Immutable
@NoArgsConstructor(access = PROTECTED)
public abstract non-sealed class NumberRef extends TypedRef
{
    /**
     * @throws TypeMismatchException if the number is not a valid integer
     * @see #readInteger()
     */
    public abstract Value<Integer> intoInteger();

    /**
     * @throws TypeMismatchException if the number is not a valid long
     * @see #readLong()
     */
    public abstract Value<Long> intoLong();

    /**
     * @throws TypeMismatchException if the number is not a valid short
     * @see #readShort()
     */
    public abstract Value<Short> intoShort();

    /**
     * @throws TypeMismatchException if the number is not a valid double
     * @see #readDouble()
     */
    public abstract Value<Double> intoDouble();

    /**
     * @throws TypeMismatchException if the number is not a valid BigDecimal
     * @see #readBigDecimal()
     */
    public abstract Value<BigDecimal> intoBigDecimal();

    /**
     * @throws TypeMismatchException if the number is not a valid BigInteger
     * @see #readBigInteger()
     */
    public abstract Value<BigInteger> intoBigInteger();

    /**
     * @throws TypeMismatchException if the number is not a valid integer
     * @see #intoInteger()
     */
    public int readInteger()
    {
        return intoInteger().read();
    }

    /**
     * @throws TypeMismatchException if the number is not a valid long
     * @see #intoLong()
     */
    public long readLong()
    {
        return intoLong().read();
    }

    /**
     * @throws TypeMismatchException if the number is not a valid short
     * @see #intoShort()
     */
    public short readShort()
    {
        return intoShort().read();
    }

    /**
     * @throws TypeMismatchException if the number is not a valid double
     * @see #intoDouble()
     */
    public double readDouble()
    {
        return intoDouble().read();
    }

    /**
     * @throws TypeMismatchException if the number is not a valid BigDecimal
     * @see #intoBigDecimal()
     */
    public BigDecimal readBigDecimal()
    {
        return intoBigDecimal().read();
    }

    /**
     * @throws TypeMismatchException if the number is not a valid BigInteger
     * @see #intoBigInteger()
     */
    public BigInteger readBigInteger()
    {
        return intoBigInteger().read();
    }
}
