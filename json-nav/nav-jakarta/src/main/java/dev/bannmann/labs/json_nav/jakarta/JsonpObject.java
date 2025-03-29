package dev.bannmann.labs.json_nav.jakarta;

import java.util.Optional;

import jakarta.json.JsonObject;
import jakarta.json.JsonValue;

import lombok.RequiredArgsConstructor;

import dev.bannmann.labs.json_nav.AnyRef;
import dev.bannmann.labs.json_nav.MissingElementException;
import dev.bannmann.labs.json_nav.ObjectRef;

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
    public AnyRef obtain(String name)
    {
        return tryGet(name).orElseThrow(MissingElementException::new);
    }

    @Override
    public AnyRef obtain(String firstLevel, String... moreLevels)
    {
        if (moreLevels.length == 0)
        {
            return obtain(firstLevel);
        }

        ObjectRef currentObject = obtainObject(firstLevel);
        for (int i = 0; i < moreLevels.length - 1; i++)
        {
            currentObject = currentObject.obtainObject(moreLevels[i]);
        }

        return currentObject.obtain(moreLevels[moreLevels.length - 1]);
    }
}
