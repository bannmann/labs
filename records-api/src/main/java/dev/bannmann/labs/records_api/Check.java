package dev.bannmann.labs.records_api;

import lombok.NonNull;
import lombok.Value;

import org.jooq.Condition;
import org.jooq.Field;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@Value
@NullMarked
final class Check
{
    Condition failureCondition;

    CheckReason reason;

    @Nullable
    Field<?> field;

    @Nullable
    String name;

    public Check(@NonNull Condition failureCondition, @NonNull CheckReason reason, @NonNull Field<?> field)
    {
        this.failureCondition = failureCondition;
        this.reason = reason;
        this.field = field;
        name = null;
    }

    public Check(@NonNull Condition failureCondition, @NonNull CheckReason reason, @NonNull String name)
    {
        this.failureCondition = failureCondition;
        this.reason = reason;
        this.name = name;
        field = null;
    }

    public Condition getSuccessCondition()
    {
        return failureCondition.not();
    }

    /**
     * @return the field (if given) or the user-provided name of this check
     */
    public CheckLabel getLabel()
    {
        String result = name;
        if (field != null)
        {
            result = field.getQualifiedName()
                .last();
        }
        return new CheckLabel(result);
    }
}
