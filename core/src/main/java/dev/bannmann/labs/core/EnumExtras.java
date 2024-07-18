package dev.bannmann.labs.core;

import java.util.Optional;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

import org.jspecify.annotations.Nullable;

import com.google.common.base.Enums;
import com.google.errorprone.annotations.CheckReturnValue;

@UtilityClass
public class EnumExtras
{
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class FluentCheck<E extends @Nullable Enum<E>>
    {
        private final @Nullable E value;

        /**
         * @return {@code true} if the value to check is non-null and identical to one of the given values
         *
         * @throws NullPointerException if any argument is {@code null}
         */
        public boolean anyOf(@NonNull E e1, @NonNull E e2)
        {
            return value == e1 || value == e2;
        }

        /**
         * @return {@code true} if the value to check is non-null and identical to one of the given values
         *
         * @throws NullPointerException if any argument or element in {@code more} is {@code null}
         */
        @SafeVarargs
        @SuppressWarnings({ "java:S2333", "java:S1695" })
        public final boolean anyOf(@NonNull E e1, @NonNull E e2, E... more)
        {
            if (value == e1 || value == e2)
            {
                return true;
            }

            for (E e : more)
            {
                if (e == null)
                {
                    // We want to be consistent with @NonNull checks, so the advice by java:S1695 doesn't apply here.
                    throw new NullPointerException();
                }
                if (value == e)
                {
                    return true;
                }
            }

            return false;
        }

        /**
         * @return {@code true} if the value to check is not identical to any of the given values, or if it is
         * {@code null}.
         *
         * @throws NullPointerException if any argument is {@code null}
         */
        public boolean noneOf(@NonNull E e1, @NonNull E e2)
        {
            return value != e1 && value != e2;
        }

        /**
         * @return {@code true} if the value to check is not identical to any of the given values, or if it is
         * {@code null}.
         *
         * @throws NullPointerException if any argument or element in {@code more} is {@code null}
         */
        @SafeVarargs
        @SuppressWarnings("java:S2333")
        public final boolean noneOf(@NonNull E e1, @NonNull E e2, E... more)
        {
            return !anyOf(e1, e2, more);
        }
    }

    @CheckReturnValue
    public static <E extends @Nullable Enum<E>> FluentCheck<E> is(E value)
    {
        return new FluentCheck<>(value);
    }

    @CheckReturnValue
    public static <T extends Enum<T>> Optional<T> getIfPresent(Class<T> enumClass, String value)
    {
        return Enums.getIfPresent(enumClass, value)
            .toJavaUtil();
    }
}
