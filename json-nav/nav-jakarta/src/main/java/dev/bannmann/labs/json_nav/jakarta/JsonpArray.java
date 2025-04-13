package dev.bannmann.labs.json_nav.jakarta;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import com.google.common.collect.Lists;
import dev.bannmann.labs.json_nav.AnyRef;
import dev.bannmann.labs.json_nav.ArrayRef;
import dev.bannmann.labs.json_nav.JsonNode;
import jakarta.json.JsonArray;
import jakarta.json.JsonValue;

@EqualsAndHashCode
@RequiredArgsConstructor
class JsonpArray<T extends JsonNode> implements ArrayRef<T>, AnyRef
{
    private final JsonArray target;
    private final Class<T> elementClass;

    @Override
    public boolean isArray()
    {
        return true;
    }

    @Override
    public <E extends JsonNode> ArrayRef<E> asArray(Class<E> elementClass)
    {
        return new JsonpArray<>(target, elementClass);
    }

    @Override
    public boolean isEmpty()
    {
        return target.isEmpty();
    }

    @Override
    public Stream<T> stream()
    {
        return target.stream()
            .map(this::wrapElement);
    }

    private T wrapElement(JsonValue input)
    {
        return elementClass.cast(Jsonp.wrap(input));
    }

    @Override
    public List<T> toList()
    {
        return Lists.transform(target.getValuesAs(JsonValue.class), this::wrapElement);
    }

    @Override
    public Iterator<T> iterator()
    {
        return toList().iterator();
    }

    @Override
    public int size()
    {
        return target.size();
    }

    @Override
    public String getRawJson()
    {
        return target.toString();
    }
}
