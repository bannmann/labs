package dev.bannmann.labs.records_api;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import dev.bannmann.labs.annotations.SuppressWarningsRationale;
import dev.bannmann.labs.annotations.UpstreamCandidate;

/**
 * Simplifies implementing wrapper classes to avoid "stringly typed" APIs. <br>
 * <br>
 * Instances never contain {@code null} and are immutable. Use {@link #toString()} or {@link CharSequence} methods to
 * access their contents.<br>
 * <br>
 * <h3>Usage example</h3>
 * <pre>{@code public final class Label extends StringWrapper
 * {
 *     public Label(String contents)
 *     {
 *         super(contents);
 *     }
 * }</pre>
 */
@UpstreamCandidate("Mizool")
@RequiredArgsConstructor
public abstract class StringWrapper implements CharSequence
{
    @NonNull
    @Delegate
    private final String contents;

    @Override
    public String toString()
    {
        return contents;
    }

    @Override
    @SuppressWarnings("EqualsGetClass")
    @SuppressWarningsRationale("We don't want equals to work across different StringWrapper subclasses")
    public final boolean equals(Object obj)
    {
        if (obj != null && getClass().equals(obj.getClass()))
        {
            return ((StringWrapper) obj).contents.equals(contents);
        }
        return false;
    }

    @Override
    public final int hashCode()
    {
        return contents.hashCode();
    }
}
