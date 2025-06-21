package dev.bannmann.labs.json_nav.gson;

import lombok.experimental.UtilityClass;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.bannmann.labs.json_nav.AnyRef;
import dev.bannmann.labs.json_nav.TypedRef;

@UtilityClass
class GsonAdapter
{
    static AnyRef wrap(JsonElement target)
    {
        if (target instanceof JsonArray jsonArray)
        {
            return new GsonArray<>(jsonArray, TypedRef.class);
        }

        if (target instanceof JsonObject jsonObject)
        {
            return new GsonObject(jsonObject);
        }

        if (target instanceof JsonPrimitive jsonPrimitive)
        {
            if (jsonPrimitive.isString())
            {
                return new GsonString(jsonPrimitive);
            }
            if (jsonPrimitive.isNumber())
            {
                return new GsonNumber(jsonPrimitive);
            }
            if (jsonPrimitive.isBoolean())
            {
                return getBooleanRef(jsonPrimitive);
            }
        }

        if (target instanceof JsonNull)
        {
            return Constants.NULL;
        }

        throw new IllegalArgumentException("Unsupported element type: " +
                                           target.getClass()
                                               .getName());
    }

    private static Constants.BooleanRefImpl getBooleanRef(JsonPrimitive jsonPrimitive)
    {
        return jsonPrimitive.getAsBoolean()
            ? Constants.TRUE
            : Constants.FALSE;
    }
}
