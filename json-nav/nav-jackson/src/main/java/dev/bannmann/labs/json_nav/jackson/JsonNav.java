package dev.bannmann.labs.json_nav.jackson;

import lombok.NonNull;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.bannmann.labs.json_nav.ArrayRef;
import dev.bannmann.labs.json_nav.JsonNode;
import dev.bannmann.labs.json_nav.ObjectRef;

public class JsonNav
{
    public ObjectRef wrap(@NonNull ObjectNode source)
    {
        return Jackson.wrap(source)
            .asObject();
    }

    public <E extends JsonNode> ArrayRef<E> wrap(@NonNull ArrayNode source, @NonNull Class<E> elementClass)
    {
        return Jackson.wrap(source)
            .asArray(elementClass);
    }
}
