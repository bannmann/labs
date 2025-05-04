package dev.bannmann.labs.json_nav;

import java.util.function.Predicate;

import com.google.errorprone.annotations.Immutable;

@Immutable
public sealed interface NullRef extends JsonNode permits Constants.NullRefImpl
{
    Predicate<AnyRef> EXCLUDE = Predicate.not(AnyRef::isNull);
}
