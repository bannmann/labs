package com.github.bannmann.labs.records_api;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import com.github.bannmann.labs.annotations.UpstreamCandidate;

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
}
