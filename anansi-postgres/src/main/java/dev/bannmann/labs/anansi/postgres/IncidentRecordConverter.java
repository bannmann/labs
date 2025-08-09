package dev.bannmann.labs.anansi.postgres;

import java.util.List;

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import org.jspecify.annotations.Nullable;

import com.github.mizool.core.converter.EnumConverter;
import com.github.mizool.core.converter.IdentifierConverter;
import dev.bannmann.anansi.core.Incident;
import dev.bannmann.anansi.core.ThrowableData;
import dev.bannmann.labs.anansi.postgres.generated.tables.records.IncidentRecord;

@RequiredArgsConstructor(onConstructor_ = @Inject, access = AccessLevel.PROTECTED)
class IncidentRecordConverter
{
    private final IdentifierConverter identifierConverter;
    private final EnumConverter enumConverter;

    public IncidentRecord fromPojo(Incident pojo)
    {
        IncidentRecord result = new IncidentRecord();
        result.setId(identifierConverter.fromPojo(pojo.getId()));
        result.setTimestamp(pojo.getTimestamp());
        result.setFingerprintId(pojo.getFingerprint());
        result.setSeverity(enumConverter.fromPojo(pojo.getSeverity()));
        result.setThrowableDetails(createThrowableDetailsReport(pojo.getThrowableDetails()));
        result.setBuild(pojo.getApplicationBuildInfo());
        return result;
    }

    /**
     * TODO this isn't postgres specific and should therefore move to core at some point
     */
    private String createThrowableDetailsReport(List<ThrowableData> throwableDetails)
    {
        StringBuilder result = new StringBuilder();

        ThrowableData previous = null;
        for (ThrowableData current : throwableDetails)
        {
            append(current, result, previous);

            previous = current;
        }

        return result.toString();
    }

    private void append(ThrowableData current, StringBuilder target, @Nullable ThrowableData previous)
    {
        if (!target.isEmpty())
        {
            target.append("\n\n");
        }

        target.append(current.getThrowableClassName());
        target.append(" at ");
        target.append(current.getFrameData());
        if (isNewMessage(previous, current))
        {
            target.append(":\n");
            target.append(current.getThrowableMessage());
        }
    }

    /**
     * @return whether current message is non-null and equals neither the previous one nor a trivial
     * "causeClass: causeMessage" (with causeMessage being non-null) or "causeClass" (if causeMessage is null)
     */
    private boolean isNewMessage(@Nullable ThrowableData previous, ThrowableData current)
    {
        if (previous == null)
        {
            return true;
        }

        String currentMessage = current.getThrowableMessage();
        if (currentMessage == null)
        {
            return false;
        }

        String previousMessage = previous.getThrowableMessage();
        String previousClass = previous.getThrowableClassName();
        if (previousMessage == null && currentMessage.equals(previousClass))
        {
            return false;
        }

        String previousClassAndMessage = previousClass + ": " + previousMessage;
        return !(currentMessage.equals(previousMessage) || currentMessage.equals(previousClassAndMessage));
    }
}
