package dev.bannmann.labs.json_nav.jakarta;

import lombok.NonNull;

import dev.bannmann.labs.json_nav.ArrayRef;
import dev.bannmann.labs.json_nav.JsonNode;
import dev.bannmann.labs.json_nav.ObjectRef;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

public class JsonNav
{
    public ObjectRef wrap(@NonNull JsonObject source)
    {
        return Jsonp.wrap(source)
            .asObject();
    }

    public <E extends JsonNode> ArrayRef<E> wrap(@NonNull JsonArray source, @NonNull Class<E> elementClass)
    {
        return Jsonp.wrap(source)
            .asArray(elementClass);
    }
}
