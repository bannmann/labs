package dev.bannmann.labs.json_nav.gson;

import java.io.Reader;

import com.google.gson.JsonParser;
import dev.bannmann.labs.json_nav.ObjectRef;
import dev.bannmann.labs.json_nav.tests.AbstractAdapterTest;

public class GsonTest extends AbstractAdapterTest
{
    @Override
    protected ObjectRef load(Reader reader)
    {
        var jsonObject = JsonParser.parseReader(reader)
            .getAsJsonObject();
        return new JsonNav().wrap(jsonObject);
    }
}
