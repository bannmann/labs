package dev.bannmann.labs.records_api;

import java.util.List;

import lombok.experimental.UtilityClass;

import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jspecify.annotations.NullMarked;

import com.github.mizool.core.exception.CodeInconsistencyException;
import dev.bannmann.labs.annotations.SuppressWarningsRationale;

@UtilityClass
@NullMarked
class Tables
{
    public static <R extends Record> TableField<R, String> obtainSingleStringPrimaryKeyField(Table<R> table)
    {
        TableField<R, ?> keyField = obtainSingleKeyField(table);
        return verifyAndCast(keyField, table);
    }

    @SuppressWarnings("unchecked")
    @SuppressWarningsRationale("We verify the datatype before performing the cast")
    private static <R extends Record> TableField<R, String> verifyAndCast(TableField<R, ?> keyField, Table<R> table)
    {
        if (!keyField.getDataType()
            .isString())
        {
            throw new CodeInconsistencyException("Table %s has a non-string primary key".formatted(table.getName()));
        }

        return (TableField<R, String>) keyField;
    }

    private static <R extends Record> TableField<R, ?> obtainSingleKeyField(Table<R> table)
    {
        var keyFields = obtainKeyFields(table);
        if (keyFields.size() > 1)
        {
            throw new CodeInconsistencyException("Table %s has a multi-column primary key".formatted(table.getName()));
        }

        return keyFields.getFirst();
    }

    private static <R extends Record> List<TableField<R, ?>> obtainKeyFields(Table<R> table)
    {
        var primaryKey = obtainPrimaryKey(table);
        return primaryKey.getFields();
    }

    private static <R extends Record> UniqueKey<R> obtainPrimaryKey(Table<R> table)
    {
        var result = table.getPrimaryKey();
        if (result == null)
        {
            throw new CodeInconsistencyException("Table %s does not have a primary key".formatted(table.getName()));
        }
        return result;
    }
}
