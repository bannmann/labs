package dev.bannmann.labs.watchdog;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jspecify.annotations.Nullable;

import dev.bannmann.labs.annotations.ImplementationNote;
import dev.bannmann.labs.core.Counter;
import dev.bannmann.labs.core.NullSafeLegacy;

@Named
@Singleton
@Slf4j
@RequiredArgsConstructor(onConstructor_ = { @Inject })
class LogTracker
{
    private record State(String pluginArtifactId, Counter counter)
    {
    }

    private static final ThreadLocal<State> THREAD_STATE = new ThreadLocal<>();

    private final Configuration configuration;

    public void onBeginExecution(String pluginArtifactId)
    {
        THREAD_STATE.set(new State(pluginArtifactId, new Counter()));
    }

    public void reset()
    {
        THREAD_STATE.remove();
    }

    public void trackWarning(@Nullable CharSequence text)
    {
        if (!configuration.isEnabled())
        {
            return;
        }

        trackWarning(NullSafeLegacy.tryGet(text, CharSequence::toString));
    }

    @ImplementationNote("parameter `text` is needed for FIXME comment below ")
    public void trackWarning(@Nullable String text)
    {
        if (!configuration.isEnabled())
        {
            return;
        }

        var state = THREAD_STATE.get();

        if (matchesExclusion(text, state))
        {
            return;
        }

        // TODO add debug mode that logs each warning passed to this method and whether it was counted

        state.counter()
            .increment();
    }

    private boolean matchesExclusion(@Nullable String text, State state)
    {
        var exclusions = configuration.getExclusions();
        return exclusions != null &&
               exclusions.stream()
                   .anyMatch(exclusion -> exclusion.matches(state.pluginArtifactId(), text));
    }

    public boolean hasWarnings()
    {
        return THREAD_STATE.get()
                   .counter()
                   .getCount() > 0;
    }
}
