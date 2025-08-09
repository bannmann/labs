package dev.bannmann.labs.watchdog;

import java.util.regex.Pattern;

import lombok.Data;

import org.jspecify.annotations.Nullable;

import dev.bannmann.labs.annotations.SuppressWarningsRationale;

@Data
public class Exclusion
{
    private @Nullable String artifactId;

    private @Nullable String messagePattern;

    @SuppressWarnings("java:S116")
    @SuppressWarningsRationale("Unusual name is intended to quasi-hide this from configuration via Maven POM")
    private @Nullable Pattern $pattern;

    public void setMessagePattern(@Nullable String messagePattern)
    {
        this.messagePattern = messagePattern;

        $pattern = compile(messagePattern);
    }

    private @Nullable Pattern compile(@Nullable String messagePattern)
    {
        if (messagePattern == null)
        {
            return null;
        }

        return Pattern.compile(messagePattern);
    }

    boolean matches(String artifactId, @Nullable String message)
    {
        return matchesArtifact(artifactId) && matchesMessage(message);
    }

    private boolean matchesArtifact(String artifactId)
    {
        return artifactId.equals(this.artifactId);
    }

    private boolean matchesMessage(@Nullable String message)
    {
        return message == null ||
               $pattern == null ||
               $pattern.matcher(message)
                   .find();
    }
}
