package org.example.business;

import lombok.Builder;
import lombok.Value;

import com.github.mizool.core.Identifiable;
import com.github.mizool.core.Identifier;

@Value
@Builder(toBuilder = true)
public class SingleSignOnPrincipal implements Identifiable<SingleSignOnPrincipal>
{
    Identifier<SingleSignOnPrincipal> id;
}
