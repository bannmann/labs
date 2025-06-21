package dev.bannmann.labs.json_nav.gson;

import lombok.EqualsAndHashCode;

import com.google.errorprone.annotations.Immutable;
import com.google.gson.JsonPrimitive;
import dev.bannmann.labs.annotations.SuppressWarningsRationale;
import dev.bannmann.labs.json_nav.AnyRef;
import dev.bannmann.labs.json_nav.StringRef;

@Immutable
@EqualsAndHashCode(callSuper = false)
class GsonString extends StringRef implements AnyRef
{
    @SuppressWarnings("Immutable")
    @SuppressWarningsRationale("Gson elements are mutable, but we store a deep copy")
    private final JsonPrimitive target;

    GsonString(JsonPrimitive target)
    {
        if (!target.isString())
        {
            throw new IllegalArgumentException();
        }
        this.target = target.deepCopy();
    }

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
        return target.getAsString();
    }

    @Override
    public String getRawJson()
    {
        return target.toString();
    }
}
