package dev.bannmann.labs.json_nav.jackson;

import lombok.EqualsAndHashCode;

import com.fasterxml.jackson.databind.node.TextNode;
import com.google.errorprone.annotations.Immutable;
import dev.bannmann.labs.annotations.SuppressWarningsRationale;
import dev.bannmann.labs.json_nav.AnyRef;
import dev.bannmann.labs.json_nav.StringRef;

@Immutable
@EqualsAndHashCode(callSuper = false)
class JacksonString extends StringRef implements AnyRef
{
    @SuppressWarnings("Immutable")
    @SuppressWarningsRationale("Jackson nodes are mutable, but we store a deep copy")
    private final TextNode target;

    JacksonString(TextNode target)
    {
        this.target = target.deepCopy();
    }

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

    @Override
    public String getRawJson()
    {
        return target.toString();
    }
}
