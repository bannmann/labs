package dev.bannmann.labs.json_nav.javax;

import javax.json.JsonNumber;
import javax.json.JsonString;
import javax.json.JsonValue;

import lombok.experimental.UtilityClass;

import dev.bannmann.labs.json_nav.AnyRef;
import dev.bannmann.labs.json_nav.TypedRef;

@UtilityClass
class JsonpAdapter
{
    static AnyRef wrap(JsonValue target)
    {
        return switch (target.getValueType())
        {
            case ARRAY -> new JsonpArray<>(target.asJsonArray(), TypedRef.class);
            case OBJECT -> new JsonpObject(target.asJsonObject());
            case STRING -> new JsonpString((JsonString) target);
            case NUMBER -> new JsonpNumber((JsonNumber) target);
            case TRUE -> Constants.TRUE;
            case FALSE -> Constants.FALSE;
            case NULL -> Constants.NULL;
        };
    }
}
