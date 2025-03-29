package dev.bannmann.labs.json_nav;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public non-sealed interface ArrayRef<T extends JsonNode> extends Iterable<T>, JsonNode
{
    boolean isEmpty();

    Stream<T> stream();

    List<T> toList();

    @Override
    Iterator<T> iterator();
}
