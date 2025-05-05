package dev.bannmann.labs.json_nav.jackson;

import lombok.experimental.UtilityClass;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import dev.bannmann.labs.json_nav.AnyRef;
import dev.bannmann.labs.json_nav.Constants;
import dev.bannmann.labs.json_nav.TypeMismatchException;
import dev.bannmann.labs.json_nav.TypedRef;

@UtilityClass
class Jackson
{
    static AnyRef wrap(com.fasterxml.jackson.databind.JsonNode target)
    {
        return switch (target.getNodeType())
        {
            case ARRAY -> new JacksonArray<>(castOrReject(target, ArrayNode.class), TypedRef.class);
            case OBJECT -> new JacksonObject(castOrReject(target, ObjectNode.class));
            case STRING -> new JacksonString(castOrReject(target, TextNode.class));
            case NUMBER -> new JacksonNumber(castOrReject(target, NumericNode.class));
            case BOOLEAN -> getBooleanRef(target);
            case NULL -> Constants.NULL;
            case BINARY, MISSING, POJO ->
                throw new IllegalArgumentException("Unsupported node type: " + target.getNodeType());
        };
    }

    private static <T> T castOrReject(com.fasterxml.jackson.databind.JsonNode jsonNode, Class<T> targetClass)
    {
        if (!targetClass.isInstance(jsonNode))
        {
            throw new TypeMismatchException();
        }
        return targetClass.cast(jsonNode);
    }

    private static AnyRef getBooleanRef(com.fasterxml.jackson.databind.JsonNode jsonNode)
    {
        if (!jsonNode.isBoolean())
        {
            throw new TypeMismatchException();
        }
        return jsonNode.booleanValue()
            ? Constants.TRUE
            : Constants.FALSE;
    }
}
