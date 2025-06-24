package dev.bannmann.labs.json_nav.jakarta;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import com.google.common.collect.Lists;
import com.google.errorprone.annotations.Immutable;
import dev.bannmann.labs.annotations.SuppressWarningsRationale;
import dev.bannmann.labs.json_nav.AnyRef;
import dev.bannmann.labs.json_nav.ArrayRef;
import dev.bannmann.labs.json_nav.TypeMismatchException;
import dev.bannmann.labs.json_nav.TypedRef;
import jakarta.json.JsonArray;
import jakarta.json.JsonValue;

@Immutable
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
class JsonpArray<T extends TypedRef> extends ArrayRef<T> implements AnyRef
{
    @SuppressWarnings("Immutable")
    @SuppressWarningsRationale("jakarta.json values *are* immutable")
    private final JsonArray target;

    private final Class<T> elementClass;

    @Override
    public boolean isArray()
    {
        return true;
    }

    @Override
    public <E extends TypedRef> ArrayRef<E> asArray(Class<E> elementClass)
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
        AnyRef wrap = JsonpAdapter.wrap(input);

        try
        {
            return elementClass.cast(wrap);
        }
        catch (ClassCastException e)
        {
            throw new TypeMismatchException(e);
        }
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
