package com.github.bannmann.labs.kumuluzee.errorhandling.jackson.filter;

import javax.servlet.FilterConfig;
import javax.servlet.annotation.WebFilter;

import lombok.extern.slf4j.Slf4j;

import com.github.mizool.technology.jackson.JacksonErrorHandlingFilter;

@WebFilter("*")
@Slf4j
public class AnnotatedJacksonErrorHandlingFilter extends JacksonErrorHandlingFilter
{
    @Override
    public void init(FilterConfig filterConfig)
    {
        log.info("Initialized");
    }
}
