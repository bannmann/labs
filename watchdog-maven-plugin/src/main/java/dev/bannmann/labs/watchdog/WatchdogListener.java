package dev.bannmann.labs.watchdog;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.maven.execution.MojoExecutionEvent;
import org.apache.maven.execution.MojoExecutionListener;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

@Named
@Slf4j
@RequiredArgsConstructor(onConstructor_ = { @Inject })
final class WatchdogListener implements MojoExecutionListener
{
    private final LogTracker logTracker;
    private final Configuration configuration;

    @Override
    public void beforeMojoExecution(MojoExecutionEvent event)
    {
        try
        {
            var mojo = event.getMojo();
            Log currentLog = mojo.getLog();

            if (!currentLog.getClass()
                .getName()
                .equals(CustomMavenPluginLog.class.getName()))
            {
                var newLog = new CustomMavenPluginLog(currentLog, logTracker);
                mojo.setLog(newLog);
            }

            logTracker.onBeginExecution(event.getExecution()
                .getArtifactId());
        }
        catch (RuntimeException e)
        {
            logFailure(e);
        }
    }

    private void logFailure(RuntimeException e)
    {
        log.debug("Watchdog listener failed", e);
    }

    @Override
    public void afterMojoExecutionSuccess(MojoExecutionEvent event) throws MojoExecutionException
    {
        try
        {
            try
            {
                if (configuration.isEnabled() && logTracker.hasWarnings())
                {
                    throw new MojoExecutionException("Watchdog aborted execution as warnings were logged (see above)");
                }
            }
            finally
            {
                logTracker.reset();
            }
        }
        catch (RuntimeException e)
        {
            logFailure(e);
        }
    }

    @Override
    public void afterExecutionFailure(MojoExecutionEvent event)
    {
        try
        {
            logTracker.reset();
        }
        catch (RuntimeException e)
        {
            logFailure(e);
        }
    }
}
