package dev.bannmann.labs.json_nav.gson;

import java.util.Optional;

import lombok.EqualsAndHashCode;

import com.google.errorprone.annotations.Immutable;
import com.google.gson.JsonObject;
import dev.bannmann.labs.annotations.SuppressWarningsRationale;
import dev.bannmann.labs.json_nav.AnyRef;
import dev.bannmann.labs.json_nav.ObjectRef;

@Immutable
@EqualsAndHashCode(callSuper = false)
class GsonObject extends ObjectRef implements AnyRef
{
    @SuppressWarnings("Immutable")
    @SuppressWarningsRationale("Gson elements are mutable, but we store a deep copy")
    private final JsonObject target;

    GsonObject(JsonObject target)
    {
        this.target = target.deepCopy();
    }

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
        var jsonElement = target.get(name);
        if (jsonElement == null)
        {
            // We have no mapping
            return Optional.empty();
        }

        // We have a mapping, but it may be a JSON null literal
        return Optional.of(GsonAdapter.wrap(jsonElement));
    }

    @Override
    public String getRawJson()
    {
        return target.toString();
    }
}
