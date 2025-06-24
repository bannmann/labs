package dev.bannmann.labs.json_nav.javax;

import java.io.Reader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.stream.JsonParser;

import dev.bannmann.labs.json_nav.ObjectRef;
import dev.bannmann.labs.json_nav.tests.AbstractAdapterTest;

public class JavaxJsonpTest extends AbstractAdapterTest
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
