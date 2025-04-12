package dev.bannmann.labs.json_nav.jakarta;

import lombok.experimental.UtilityClass;

import dev.bannmann.labs.json_nav.AnyRef;
import dev.bannmann.labs.json_nav.Constants;
import dev.bannmann.labs.json_nav.JsonNode;
import jakarta.json.JsonNumber;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;

@UtilityClass
class Jsonp
{
    static AnyRef wrap(JsonValue target)
    {
        return switch (target.getValueType())
        {
            case ARRAY -> new JsonpArray<>(target.asJsonArray(), JsonNode.class);
            case OBJECT -> new JsonpObject(target.asJsonObject());
            case STRING -> new JsonpString((JsonString) target);
            case NUMBER -> new JsonpNumber((JsonNumber) target);
            case TRUE -> Constants.TRUE;
            case FALSE -> Constants.FALSE;
            case NULL -> Constants.NULL;
        };
    }
}
