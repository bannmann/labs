package dev.bannmann.labs.json_nav;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

import com.google.errorprone.annotations.Immutable;
import dev.bannmann.labs.annotations.ImplementationNote;

@UtilityClass
public class Constants
{
    @Immutable
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class NullRefImpl extends NullRef implements AnyRef
    {
        @Override
        public boolean isNull()
        {
            return true;
        }

        @Override
        public NullRef asNull()
        {
            return this;
        }

        @Override
        public String getRawJson()
        {
            return "null";
        }
    }

    @Immutable
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class BooleanRefImpl extends BooleanRef implements AnyRef
    {
        private final Boolean value;

        @Override
        public boolean isBoolean()
        {
            return true;
        }

        @Override
        public BooleanRef asBoolean()
        {
            return this;
        }

        @Override
        public Boolean read()
        {
            return value;
        }

        @Override
        public String getRawJson()
        {
            return Boolean.TRUE.equals(value)
                ? "true"
                : "false";
        }
    }

    @ImplementationNote("Field cannot be a `NullRef` as it needs to be usable in places where `AnyRef` is required")
    public static final NullRefImpl NULL = new NullRefImpl();

    @ImplementationNote("Field cannot be a `BooleanRef` as it needs to be usable in places where `AnyRef` is required")
    public static final BooleanRefImpl TRUE = new BooleanRefImpl(Boolean.TRUE);

    @ImplementationNote("Field cannot be a `BooleanRef` as it needs to be usable in places where `AnyRef` is required")
    public static final BooleanRefImpl FALSE = new BooleanRefImpl(Boolean.FALSE);
}
