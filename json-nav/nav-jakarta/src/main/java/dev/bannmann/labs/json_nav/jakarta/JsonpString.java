package dev.bannmann.labs.json_nav.jakarta;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import com.google.errorprone.annotations.Immutable;
import dev.bannmann.labs.annotations.SuppressWarningsRationale;
import dev.bannmann.labs.json_nav.AnyRef;
import dev.bannmann.labs.json_nav.StringRef;
import jakarta.json.JsonString;

@Immutable
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
class JsonpString extends StringRef implements AnyRef
{
    @SuppressWarnings("Immutable")
    @SuppressWarningsRationale("jakarta.json values *are* immutable")
    private final JsonString target;

    @Override
    public boolean isString()
    {
        return true;
    }

    @Override
    public StringRef asString()
    {
        return this;
    }

    @Override
    public String read()
    {
        return target.getString();
    }

    @Override
    public String getRawJson()
    {
        return target.toString();
    }
}
