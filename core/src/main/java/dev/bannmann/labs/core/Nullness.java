package dev.bannmann.labs.core;

import java.util.function.Supplier;

import lombok.experimental.UtilityClass;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.github.mizool.core.exception.CodeInconsistencyException;
import com.github.mizool.core.exception.InvalidBackendReplyException;
import dev.bannmann.labs.annotations.SuppressWarningsRationale;
import dev.bannmann.labs.annotations.UpstreamCandidate;

/**
 * Helper methods related to jSpecify nullness checking.
 */
@UtilityClass
@UpstreamCandidate("Mizool")
public class Nullness
{
    /**
     * Raises a {@link CodeInconsistencyException} if the value turns out to be {@code null}.
     *
     * @param value the value that could formally be {@code null}, but will not be in practice.
     * @param <T> the type of the given value
     *
     * @return the given value if it is indeed non-{@code null}
     *
     * @throws CodeInconsistencyException if the value is null
     */
    public static <T> T guaranteeNonNull(@Nullable T value)
    {
        if (value == null)
        {
            throw new CodeInconsistencyException(
                "Encountered null value that should have been prevented by build-time checks");
        }

        return value;
    }

    /**
     * Raises an {@link IllegalArgumentException} if the retrieved value is {@code null}.
     *
     * @param valueSupplier the call that gets the value from an argument passed to the caller
     * @param argumentName the argument name to use for the exception message
     * @param <T> the type of the value
     *
     * @return the non-{@code null} value
     *
     * @throws IllegalArgumentException if the value is {@code null}
     */
    @SuppressWarnings("java:S2259")
    @SuppressWarningsRationale("Sonar thinks valueSupplier can be null")
    public static <T extends @Nullable Object> @NonNull T obtainNonNullFromArgument(
        Supplier<T> valueSupplier,
        String argumentName)
    {
        T value = valueSupplier.get();
        if (value == null)
        {
            throw new IllegalArgumentException("Argument '%s' is invalid".formatted(argumentName));
        }

        return value;
    }

    /**
     * Raises an {@link InvalidBackendReplyException} if the value turns out to be {@code null}.
     *
     * @param value the value that the backend system promised would not be {@code null}
     * @param fieldName the field name to use for the exception message, e.g. {@code ClassOfT.Fields.fieldName} when
     * using Lombok's {@code @FieldNameConstants}.
     * @param <T> the type of the given value
     *
     * @return the given value if it is indeed non-{@code null}
     *
     * @throws InvalidBackendReplyException if the value is null
     */
    public static <T> T obtainBackendReplyField(@Nullable T value, String fieldName)
    {
        if (value == null)
        {
            throw new InvalidBackendReplyException("Missing value for field '%s'".formatted(fieldName));
        }

        return value;
    }

    public static <T> T getIfNonNullOrFallback(@Nullable T value, T fallback)
    {
        if (value == null)
        {
            return fallback;
        }

        return value;
    }

    public static <T> T getIfNonNullOrFallback(@Nullable T value, Supplier<T> fallbackSupplier)
    {
        if (value == null)
        {
            return fallbackSupplier.get();
        }

        return value;
    }
}
