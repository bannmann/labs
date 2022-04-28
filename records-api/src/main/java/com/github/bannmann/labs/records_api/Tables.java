package com.github.bannmann.labs.records_api;

import java.util.List;

import lombok.experimental.UtilityClass;

import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UpdatableRecord;

import com.github.mizool.core.exception.CodeInconsistencyException;

@UtilityClass
class Tables
{
    public <R extends UpdatableRecord<?>> TableField<R, String> obtainPrimaryKey(Table<R> table)
    {
        List<TableField<R, ?>> keyFields = table.getPrimaryKey()
            .getFields();
        if (keyFields.size() > 1)
        {
            throw new CodeInconsistencyException(String.format("Table %s has a multi-column primary key",
                table.getName()));
        }

        TableField<R, ?> keyField = keyFields.get(0);
        if (!keyField.getType()
            .equals(String.class))
        {
            throw new CodeInconsistencyException(String.format("Table %s has a non-string primary key",
                table.getName()));
        }

        @SuppressWarnings("unchecked") TableField<R, String> stringKeyField = (TableField<R, String>) keyField;
        return stringKeyField;
    }
}
