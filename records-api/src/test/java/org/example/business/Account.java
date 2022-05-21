package org.example.business;

import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.FieldNameConstants;

import com.github.mizool.core.Identifiable;
import com.github.mizool.core.Identifier;

@Value
@Builder(toBuilder = true)
@FieldNameConstants
public class Account implements Identifiable<Account>
{
    Identifier<Account> id;

    @NonNull String displayName;

    @NonNull String email;

    Identifier<SingleSignOnPrincipal> ssoId;

    short renameCount;

    OffsetDateTime timestamp;
}
