package org.example.business;

import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import com.github.mizool.core.Identifiable;
import com.github.mizool.core.Identifier;

@Value
@Builder
public class Account implements Identifiable<Account>
{
    Identifier<Account> id;

    @NonNull String email;

    OffsetDateTime timestamp;
}
