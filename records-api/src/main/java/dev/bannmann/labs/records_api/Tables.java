package dev.bannmann.labs.records_api;

import java.util.List;

import lombok.experimental.UtilityClass;

import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.UpdatableRecord;
import org.jspecify.annotations.NullMarked;

import com.github.mizool.core.exception.CodeInconsistencyException;

@UtilityClass
@NullMarked
class Tables
{
    public static <R extends UpdatableRecord<?>> TableField<R, String> obtainSingleStringPrimaryKeyField(Table<R> table)
    {
        var keyField = obtainSingleKeyField(table);
        if (!keyField.getType()
            .equals(String.class))
        {
            throw new CodeInconsistencyException(String.format("Table %s has a non-string primary key",
                table.getName()));
        }

        @SuppressWarnings("unchecked") TableField<R, String> stringKeyField = (TableField<R, String>) keyField;
        return stringKeyField;
    }

    private static <R extends UpdatableRecord<?>> TableField<R, ?> obtainSingleKeyField(Table<R> table)
    {
        var keyFields = obtainKeyFields(table);
        if (keyFields.size() > 1)
        {
            throw new CodeInconsistencyException(String.format("Table %s has a multi-column primary key",
                table.getName()));
        }

        return keyFields.getFirst();
    }

    private static <R extends UpdatableRecord<?>> List<TableField<R, ?>> obtainKeyFields(Table<R> table)
    {
        var primaryKey = obtainPrimaryKey(table);
        return primaryKey.getFields();
    }

    private static <R extends UpdatableRecord<?>> UniqueKey<R> obtainPrimaryKey(Table<R> table)
    {
        var result = table.getPrimaryKey();
        if (result == null)
        {
            throw new CodeInconsistencyException(String.format("Table %s does not have a primary key",
                table.getName()));
        }
        return result;
    }
}
