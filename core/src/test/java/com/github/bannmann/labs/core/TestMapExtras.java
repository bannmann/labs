package com.github.bannmann.labs.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestMapExtras
{
    @Test(dataProvider = "tryGetTestData")
    @SuppressWarnings({ "OptionalUsedAsFieldOrParameterType", "unused" })
    public void testTryGet(String testName, Map<String, Integer> map, String key, Optional<Integer> expectedResult)
    {
        Optional<Integer> actual = MapExtras.tryGet(map, key);
        assertThat(actual).isEqualTo(expectedResult);
    }

    @DataProvider
    private Object[][] tryGetTestData()
    {
        return new Object[][]{
            { "empty map", Map.of(), "foo", Optional.empty() },
            { "key exists", Map.of("bar", 17), "bar", Optional.of(17) },
            { "wrong key", Map.of("bar", 17), "quux", Optional.empty() },
            { "null value", Collections.singletonMap("bar", null), "bar", Optional.empty() },
            { "null key", Collections.singletonMap(null, 17), null, Optional.of(17) }
        };
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testTryGetNullMap()
    {
        assertThatThrownBy(() -> MapExtras.tryGet(null, "key")).isInstanceOf(NullPointerException.class);
    }

    @Test(dataProvider = "tryRemoveTestData")
    @SuppressWarnings({ "OptionalUsedAsFieldOrParameterType", "unused" })
    public void testTryRemove(String testName, Map<String, Integer> map, String key, Optional<Integer> expectedResult)
    {
        Optional<Integer> actual = MapExtras.tryRemove(map, key);
        assertThat(actual).isEqualTo(expectedResult);
        assertThat(map).doesNotContainKey(key);
    }

    @SuppressWarnings("unchecked")
    @DataProvider
    private Object[][] tryRemoveTestData()
    {
        return Arrays.stream(tryGetTestData())
            .peek(objects -> objects[1] = new HashMap<>((Map<String, Integer>) objects[1]))
            .collect(Collectors.toList())
            .toArray(new Object[][]{});
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testTryRemoveNullMap()
    {
        assertThatThrownBy(() -> MapExtras.tryRemove(null, "key")).isInstanceOf(NullPointerException.class);
    }
}
