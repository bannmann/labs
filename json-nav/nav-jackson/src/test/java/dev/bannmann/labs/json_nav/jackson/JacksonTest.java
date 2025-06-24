package dev.bannmann.labs.json_nav.jackson;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.bannmann.labs.json_nav.ObjectRef;
import dev.bannmann.labs.json_nav.tests.AbstractAdapterTest;

public class JacksonTest extends AbstractAdapterTest
{
    private static final ObjectMapper
        OBJECT_MAPPER
        = new ObjectMapper().configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);

    private static final JsonNav JSON_NAV = new JsonNav();

    @Override
    protected ObjectRef load(Reader reader)
    {
        try
        {
            var objectNode = (ObjectNode) OBJECT_MAPPER.readTree(reader);
            return JSON_NAV.wrap(objectNode);
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }
}
