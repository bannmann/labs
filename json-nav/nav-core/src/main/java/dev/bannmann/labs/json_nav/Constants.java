package dev.bannmann.labs.json_nav;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

import com.google.errorprone.annotations.Immutable;

@UtilityClass
public class Constants
{
    @Immutable
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class NullRefImpl implements NullRef, AnyRef
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
    public static final class BooleanRefImpl implements BooleanRef, AnyRef
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

    public static final NullRefImpl NULL = new NullRefImpl();
    public static final BooleanRefImpl TRUE = new BooleanRefImpl(Boolean.TRUE);
    public static final BooleanRefImpl FALSE = new BooleanRefImpl(Boolean.FALSE);
}
