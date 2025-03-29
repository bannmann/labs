package dev.bannmann.labs.json_nav;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants
{
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
    }

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
    }

    public static final NullRefImpl NULL = new NullRefImpl();
    public static final BooleanRefImpl TRUE = new BooleanRefImpl(Boolean.TRUE);
    public static final BooleanRefImpl FALSE = new BooleanRefImpl(Boolean.FALSE);
}
