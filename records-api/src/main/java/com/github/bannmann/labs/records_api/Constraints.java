package com.github.bannmann.labs.records_api;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;

import org.jooq.Field;
import org.jooq.Key;
import org.jooq.Table;
import org.jooq.exception.DataAccessException;

import com.google.common.base.Throwables;

@UtilityClass
class Constraints
{
    public Optional<String> findFieldOfViolatedForeignKey(DataAccessException e, Table<?> table)
    {
        return findFieldOfViolatedKey(e, table.getReferences());
    }

    private Optional<String> findFieldOfViolatedKey(DataAccessException e, List<? extends Key<?>> keys)
    {
        String rootCauseMessage = Throwables.getRootCause(e)
            .getMessage();
        for (var key : keys)
        {
            if (rootCauseMessage.contains(key.getName()))
            {
                // We return only a string because many constraints only consist of one column.
                return Optional.of(key.getFields()
                    .stream()
                    .map(Field::getName)
                    .collect(Collectors.joining("+")));
            }
        }
        return Optional.empty();
    }

    public Optional<String> findFieldOfViolatedUniqueOrPrimaryKey(DataAccessException e, Table<?> table)
    {
        return findFieldOfViolatedKey(e, table.getKeys());
    }
}
