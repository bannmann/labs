package dev.bannmann.labs.json_nav.jackson;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.databind.node.TextNode;
import dev.bannmann.labs.json_nav.AnyRef;
import dev.bannmann.labs.json_nav.StringRef;

@EqualsAndHashCode
@RequiredArgsConstructor
class JacksonString implements StringRef, AnyRef
{
    private final TextNode target;

    @Override
    public boolean isString()
    {
        return true;
    }

    @Override
    public StringRef asString()
    {
        return this;
    }

    @Override
    public String read()
    {
        return target.asText();
    }
}
