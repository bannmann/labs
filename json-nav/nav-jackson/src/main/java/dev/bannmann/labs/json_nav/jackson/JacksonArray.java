package dev.bannmann.labs.json_nav.jackson;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Iterators;
import dev.bannmann.labs.json_nav.AnyRef;
import dev.bannmann.labs.json_nav.ArrayRef;
import dev.bannmann.labs.json_nav.JsonNode;

@EqualsAndHashCode
@RequiredArgsConstructor
class JacksonArray<T extends JsonNode> implements ArrayRef<T>, AnyRef
{
    private final ArrayNode target;
    private final Class<T> elementClass;

    @Override
    public boolean isArray()
    {
        return true;
    }

    @Override
    public <E extends JsonNode> ArrayRef<E> asArray(Class<E> elementClass)
    {
        return new JacksonArray<>(target, elementClass);
    }

    @Override
    public boolean isEmpty()
    {
        return target.isEmpty();
    }

    @Override
    public Stream<T> stream()
    {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED), false);
    }

    private T wrapElement(com.fasterxml.jackson.databind.JsonNode input)
    {
        return elementClass.cast(Jackson.wrap(input));
    }

    @Override
    public List<T> toList()
    {
        return new AbstractList<>()
        {
            @Override
            public T get(int index)
            {
                return wrapElement(target.get(index));
            }

            @Override
            public int size()
            {
                return target.size();
            }
        };
    }

    @Override
    public Iterator<T> iterator()
    {
        return Iterators.transform(target.iterator(), this::wrapElement);
    }
}
