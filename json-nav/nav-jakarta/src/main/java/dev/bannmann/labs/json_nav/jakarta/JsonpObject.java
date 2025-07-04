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
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
class JsonpObject extends ObjectRef implements AnyRef
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
    public Optional<AnyRef> tryGetAny(String name)
    {
        JsonValue jsonValue = target.get(name);
        if (jsonValue == null)
        {
            // We have no mapping
            return Optional.empty();
        }

        // We have a mapping, but it may be a JSON null literal
        return Optional.of(JsonpAdapter.wrap(jsonValue));
    }

    @Override
    public String getRawJson()
    {
        return target.toString();
    }
}
