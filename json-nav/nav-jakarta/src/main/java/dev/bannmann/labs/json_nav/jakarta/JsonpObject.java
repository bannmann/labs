package dev.bannmann.labs.json_nav.jakarta;

import java.util.Optional;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import dev.bannmann.labs.json_nav.AnyRef;
import dev.bannmann.labs.json_nav.ObjectRef;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;

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
}
