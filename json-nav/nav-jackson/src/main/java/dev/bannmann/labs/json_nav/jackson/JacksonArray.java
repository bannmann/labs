package dev.bannmann.labs.json_nav.jackson;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.EqualsAndHashCode;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Iterators;
import com.google.errorprone.annotations.Immutable;
import dev.bannmann.labs.annotations.SuppressWarningsRationale;
import dev.bannmann.labs.json_nav.AnyRef;
import dev.bannmann.labs.json_nav.ArrayRef;
import dev.bannmann.labs.json_nav.JsonNode;

@Immutable
@EqualsAndHashCode(callSuper = false)
final class JacksonArray<T extends JsonNode> extends ArrayRef<T> implements AnyRef
{
    @SuppressWarnings("Immutable")
    @SuppressWarningsRationale("Jackson nodes are mutable, but we store a deep copy")
    private final ArrayNode target;

    private final Class<T> elementClass;

    JacksonArray(ArrayNode target, Class<T> elementClass)
    {
        this.target = target.deepCopy();
        this.elementClass = elementClass;
    }

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
