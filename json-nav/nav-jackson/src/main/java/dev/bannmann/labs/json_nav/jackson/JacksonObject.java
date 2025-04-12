package dev.bannmann.labs.json_nav.jackson;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.bannmann.labs.json_nav.AnyRef;
import dev.bannmann.labs.json_nav.ObjectRef;

@RequiredArgsConstructor
class JacksonObject implements ObjectRef, AnyRef
{
    private final ObjectNode target;

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
        JsonNode jsonNode = target.get(name);
        if (jsonNode == null || jsonNode.isNull())
        {
            return Optional.empty();
        }

        return Optional.of(Jackson.wrap(jsonNode));
    }
}
