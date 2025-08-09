package dev.bannmann.labs.watchdog;

import java.util.List;

import javax.inject.Inject;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.jspecify.annotations.Nullable;

import com.github.mizool.core.exception.CodeInconsistencyException;

@Mojo(name = "watch", defaultPhase = LifecyclePhase.VALIDATE)
public final class WatchdogMojo extends AbstractMojo
{
    @Inject
    private @Nullable Configuration configuration;

    @Parameter
    private @Nullable List<Exclusion> exclusions;

    @Override
    public void execute()
    {
        if (configuration == null)
        {
            throw new CodeInconsistencyException();
        }
        configuration.setEnabled(true);
        configuration.setExclusions(exclusions);
    }
}
