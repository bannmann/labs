package dev.bannmann.labs.watchdog;

import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import lombok.Synchronized;

import org.jspecify.annotations.Nullable;

@Named
@Singleton
final class Configuration
{
    private boolean enabled;

    private @Nullable List<Exclusion> exclusions;

    @Synchronized
    public boolean isEnabled()
    {
        return enabled;
    }

    @Synchronized
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Synchronized
    public @Nullable List<Exclusion> getExclusions()
    {
        return exclusions;
    }

    @Synchronized
    public void setExclusions(@Nullable List<Exclusion> exclusions)
    {
        this.exclusions = exclusions != null
            ? List.copyOf(exclusions)
            : null;
    }
}
