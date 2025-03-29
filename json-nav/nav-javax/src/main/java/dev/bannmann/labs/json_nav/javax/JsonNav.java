package dev.bannmann.labs.json_nav.javax;

import javax.json.JsonArray;
import javax.json.JsonObject;

import lombok.NonNull;

import dev.bannmann.labs.json_nav.ArrayRef;
import dev.bannmann.labs.json_nav.JsonNode;
import dev.bannmann.labs.json_nav.ObjectRef;

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
