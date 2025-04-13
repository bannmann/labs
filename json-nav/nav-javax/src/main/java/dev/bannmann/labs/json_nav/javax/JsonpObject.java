package dev.bannmann.labs.json_nav.javax;

import java.util.Optional;

import javax.json.JsonObject;
import javax.json.JsonValue;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import dev.bannmann.labs.json_nav.AnyRef;
import dev.bannmann.labs.json_nav.ObjectRef;

@EqualsAndHashCode
@RequiredArgsConstructor
class JsonpObject implements ObjectRef, AnyRef
{
    private final JsonObject target;

    @Override
    public boolean isObject()
    {
        return true;
    }

    @Override
    public ObjectRef asObject()
    {
        return this;
    }

    @Override
    public Optional<AnyRef> tryGet(String name)
    {
        JsonValue jsonValue = target.get(name);
        if (jsonValue == null || jsonValue == JsonValue.NULL)
        {
            return Optional.empty();
        }

        return Optional.of(Jsonp.wrap(jsonValue));
    }

    @Override
    public String getRawJson()
    {
        return target.toString();
    }
}
