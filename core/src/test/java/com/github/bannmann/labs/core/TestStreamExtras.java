package com.github.bannmann.labs.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.github.mizool.core.exception.ReductionException;

public class TestStreamExtras
{
    @Test(dataProvider = "validStreamTestData")
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public void testValidStream(String testCase, List<String> input, Optional<String> expected)
    {
        Optional<String> actual = input.stream()
            .reduce(StreamExtras.atMostOneThrowing(ReductionException::new));
        assertThat(actual).isEqualTo(expected);
    }

    @DataProvider
    private Object[][] validStreamTestData()
    {
        return new Object[][]{
            { "empty list", List.of(), Optional.empty() }, { "one-element list", List.of("foo"), Optional.of("foo") }
        };
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void testTooManyElements()
    {
        assertThatThrownBy(() -> Stream.of("bar", "quux")
            .reduce(StreamExtras.atMostOneThrowing(ReductionException::new))).isInstanceOf(ReductionException.class);
    }
}
