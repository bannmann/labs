package com.github.bannmann.labs.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestClassExtras
{
    @Test(dataProvider = "tryResolveTestData")
    @SuppressWarnings({ "OptionalUsedAsFieldOrParameterType", "unused" })
    public void testTryResolve(String scenario, String className, Optional<Class<?>> expectedResult)
    {
        Optional<Class<?>> actual = ClassExtras.tryResolve(className);
        assertThat(actual).isEqualTo(expectedResult);
    }

    @DataProvider
    private Object[][] tryResolveTestData()
    {
        return new Object[][]{
            { "exists", "java.util.Date", Optional.of(Date.class) }, { "missing", "com.example.Foo", Optional.empty() }
        };
    }
}
