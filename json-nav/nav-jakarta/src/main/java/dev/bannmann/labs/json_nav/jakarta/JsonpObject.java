package dev.bannmann.labs.json_nav.jakarta;

import java.util.Optional;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import com.google.errorprone.annotations.Immutable;
import dev.bannmann.labs.annotations.SuppressWarningsRationale;
import dev.bannmann.labs.json_nav.AnyRef;
import dev.bannmann.labs.json_nav.ObjectRef;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;

@Immutable
@EqualsAndHashCode
@RequiredArgsConstructor
class JsonpObject implements ObjectRef, AnyRef
{
    @SuppressWarnings("Immutable")
    @SuppressWarningsRationale("jakarta.json values *are* immutable")
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
