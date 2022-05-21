package org.example.business;

import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldNameConstants;

import com.github.mizool.core.Identifiable;
import com.github.mizool.core.Identifier;
import com.github.mizool.core.validation.Nullable;

@Value
@Builder(toBuilder = true)
@FieldNameConstants
public class Department implements Identifiable<Department>
{
    Identifier<Department> id;

    /**
     * To allow a certain test, we intentionally allow null although the database column does not.
     */
    @Nullable
    String name;

    Identifier<Account> ownerAccountId;

    OffsetDateTime timestamp;
}
