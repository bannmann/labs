package dev.bannmann.labs.json_nav.jakarta;

import java.io.Reader;

import dev.bannmann.labs.json_nav.ObjectRef;
import dev.bannmann.labs.json_nav.tests.AbstractAdapterTest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.stream.JsonParser;

public class JakartaJsonpTest extends AbstractAdapterTest
{
    @Override
    protected ObjectRef load(Reader reader)
    {
        var jsonObject = readObject(reader);
        return new JsonNav().wrap(jsonObject);
    }

    private JsonObject readObject(Reader reader)
    {
        JsonParser parser = Json.createParser(reader);
        parser.next();
        return parser.getObject();
    }
}
