package dev.bannmann.labs.json_nav.gson;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.EqualsAndHashCode;

import com.google.common.collect.Lists;
import com.google.errorprone.annotations.Immutable;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.bannmann.labs.annotations.SuppressWarningsRationale;
import dev.bannmann.labs.json_nav.AnyRef;
import dev.bannmann.labs.json_nav.ArrayRef;
import dev.bannmann.labs.json_nav.TypedRef;

@Immutable
@EqualsAndHashCode(callSuper = false)
final class GsonArray<T extends TypedRef> extends ArrayRef<T> implements AnyRef
{
    @SuppressWarnings("Immutable")
    @SuppressWarningsRationale("Gson elements are mutable, but we store a deep copy")
    private final JsonArray target;

    private final Class<T> elementClass;

    GsonArray(JsonArray target, Class<T> elementClass)
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
    public <E extends TypedRef> ArrayRef<E> asArray(Class<E> elementClass)
    {
        return new GsonArray<>(target, elementClass);
    }

    @Override
    public boolean isEmpty()
    {
        return target.isEmpty();
    }

    @Override
    public Stream<T> stream()
    {
        return StreamSupport.stream(target.spliterator(), false)
            .map(this::wrapElement);
    }

    private T wrapElement(JsonElement input)
    {
        return elementClass.cast(GsonAdapter.wrap(input));
    }

    @Override
    public List<T> toList()
    {
        return Lists.transform(Collections.unmodifiableList(target.asList()), this::wrapElement);
    }

    @Override
    public Iterator<T> iterator()
    {
        // We cannot use target.iterator() as it supports remove()
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
