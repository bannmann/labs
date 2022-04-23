package com.github.bannmann.labs.processor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.experimental.UtilityClass;

import com.github.mizool.core.exception.CodeInconsistencyException;

@UtilityClass
class Debugging
{
    public static final Path LOG_FILE_PATH = Path.of(System.getProperty("java.io.tmpdir"), "GenerateCarriersProcessor.log");

    static
    {
        logToDisk("\n" + DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now()));
    }

    static void logToDisk(String message)
    {
        try (BufferedWriter writer = Files.newBufferedWriter(LOG_FILE_PATH,
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND))
        {
            writer.write(message);
            writer.newLine();
        }
        catch (IOException e)
        {
            // Swallow the exception - failure to log should not break compilation
        }
    }

    static void logToDisk(Exception e)
    {
        logToDisk(getExceptionWithStackTrace(e));
    }

    private String getExceptionWithStackTrace(Exception e)
    {
        try (StringWriter stringWriter = new StringWriter();
             PrintWriter printWriter = new PrintWriter(stringWriter))
        {
            e.printStackTrace(printWriter);
            return stringWriter.toString();
        }
        catch (IOException ioException)
        {
            throw new CodeInconsistencyException();
        }
    }
}
