package dev.bannmann.labs.core;

import lombok.Getter;
import lombok.ToString;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
@ToString
public final class Counter
{
    @Getter
    private int count;

    public void increment()
    {
        count++;
    }
}
