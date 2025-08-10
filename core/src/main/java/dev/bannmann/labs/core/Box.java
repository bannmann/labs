package dev.bannmann.labs.core;

import java.util.Optional;
import java.util.function.Consumer;

import lombok.NonNull;

import org.jspecify.annotations.Nullable;

import com.google.errorprone.annotations.ThreadSafe;
import dev.bannmann.labs.annotations.SuppressWarningsRationale;

/**
 * Wraps an initially-null reference. The goal is to make it easier to express the author's intention without having to
 * suppress numerous nullability-related warnings.
 *
 * @param <T> the type of the contained reference
 */
@ThreadSafe
public final class Box<T>
{
    @SuppressWarnings("java:S3077")
    @SuppressWarningsRationale(
        "`volatile` does not synchronize state _within_ T, but we only care about the reference to T itself, anyway.")
    private volatile @Nullable T value;

    /**
     * Gets the contained value or throws an
     *
     * @return the contained value
     *
     * @throws IllegalStateException if the box is empty
     */
    public T get()
    {
        T result = getOrNull();
        if (result == null)
        {
            throw new IllegalStateException();
        }

        return result;
    }

    public @Nullable T getOrNull()
    {
        return value;
    }

    public void set(@NonNull T value)
    {
        this.value = value;
    }

    public void clear()
    {
        this.value = null;
    }

    public void ifSet(Consumer<T> consumer)
    {
        // To avoid a race condition between `if` and accept(), get the volatile reference only once and use that
        T localValue = value;

        if (localValue != null)
        {
            consumer.accept(localValue);
        }
    }

    public boolean isSet()
    {
        return value != null;
    }

    public void ifEmpty(Runnable runnable)
    {
        if (isEmpty())
        {
            runnable.run();
        }
    }

    public boolean isEmpty()
    {
        return value == null;
    }

    public Optional<T> toOptional()
    {
        return Optional.ofNullable(value);
    }
}
