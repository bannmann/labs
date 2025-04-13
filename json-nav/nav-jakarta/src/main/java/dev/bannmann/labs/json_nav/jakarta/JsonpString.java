package dev.bannmann.labs.json_nav.jakarta;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import dev.bannmann.labs.json_nav.AnyRef;
import dev.bannmann.labs.json_nav.StringRef;
import jakarta.json.JsonString;

@EqualsAndHashCode
@RequiredArgsConstructor
class JsonpString implements StringRef, AnyRef
{
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
}
