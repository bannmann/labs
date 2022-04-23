package com.github.bannmann.labs.integrationtest;

import org.junit.Test;

public class IntegrationTest
{
    @Test
    public void testValidUsage()
    {
        SubjectCarryingOrigin.builder();
        SubjectCarryingDestination.builder();
        SubjectCarryingOriginAndDestination.builder();
    }
}
