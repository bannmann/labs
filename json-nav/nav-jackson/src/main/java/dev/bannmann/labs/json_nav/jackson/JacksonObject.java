package dev.bannmann.labs.json_nav.jackson;

import java.util.Optional;

import lombok.EqualsAndHashCode;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.errorprone.annotations.Immutable;
import dev.bannmann.labs.annotations.SuppressWarningsRationale;
import dev.bannmann.labs.json_nav.AnyRef;
import dev.bannmann.labs.json_nav.ObjectRef;

@Immutable
@EqualsAndHashCode
class JacksonObject implements ObjectRef, AnyRef
{
    @SuppressWarnings("Immutable")
    @SuppressWarningsRationale("Jackson nodes are mutable, but we store a deep copy")
    private final ObjectNode target;

    JacksonObject(ObjectNode target)
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
        JsonNode jsonNode = target.get(name);
        if (jsonNode == null)
        {
            // We have no mapping
            return Optional.empty();
        }

        // We have a mapping, but it may be a JSON null literal
        return Optional.of(Jackson.wrap(jsonNode));
    }

    @Override
    public String getRawJson()
    {
        return target.toString();
    }
}
