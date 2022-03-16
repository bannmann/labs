package com.github.bannmann.labs.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.testng.annotations.Test;

public class TestObjectExtras
{
    @Test
    public void testTryCastMatch()
    {
        Optional<String> actual = ObjectExtras.tryCast("Hello, World!", String.class);
        assertThat(actual).isPresent()
            .contains("Hello, World!");
    }

    @Test
    public void testTryCastMismatch()
    {
        Optional<String> actual = ObjectExtras.tryCast(List.of("Nope"), String.class);
        assertThat(actual).isEmpty();
    }

    @Test
    public void testTryCastIsNullSafe()
    {
        Optional<String> actual = ObjectExtras.tryCast(null, String.class);
        assertThat(actual).isEmpty();
    }
}
