package org.example.business;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.FieldNameConstants;

import com.github.mizool.core.Identifier;

@Value
@Builder(toBuilder = true)
@FieldNameConstants
public class DepartmentMember
{
    @NonNull Identifier<Department> departmentId;
    @NonNull Identifier<Account> accountId;
    int permissionLevel;
}
