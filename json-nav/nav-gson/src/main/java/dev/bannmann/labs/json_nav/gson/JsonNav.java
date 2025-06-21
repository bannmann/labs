package dev.bannmann.labs.json_nav.gson;

import lombok.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.bannmann.labs.json_nav.ArrayRef;
import dev.bannmann.labs.json_nav.ObjectRef;
import dev.bannmann.labs.json_nav.TypedRef;

public class JsonNav
{
    public ObjectRef wrap(@NonNull JsonObject source)
    {
        return GsonAdapter.wrap(source)
            .asObject();
    }

    public <E extends TypedRef> ArrayRef<E> wrap(@NonNull JsonArray source, @NonNull Class<E> elementClass)
    {
        return GsonAdapter.wrap(source)
            .asArray(elementClass);
    }
}
