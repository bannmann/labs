package com.github.bannmann.labs.records_api;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.OrderField;
import org.jooq.Record;
import org.jooq.ResultQuery;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import com.github.mizool.core.Identifiable;
import com.github.mizool.core.Identifier;
import com.github.mizool.core.exception.StoreLayerException;

@Slf4j
@RequiredArgsConstructor
public class SelectActionImpl<P extends Identifiable<P>, R extends Record> implements ISelectAction<P, R>
{
    private final DSLContext context;

    private Supplier<ResultQuery<R>> resultQuerySupplier;
    private Function<R, P> toPojo;
    private Condition condition = DSL.noCondition();
    private Table<R> table;
    private OrderField<?>[] orderByFields = new OrderField[]{};

    @Override
    public void convertedUsing(RecordConverter<P, R> converter)
    {
        convertedVia(converter::toPojo);
    }

    @Override
    public void convertedVia(Function<R, P> toPojo)
    {
        this.toPojo = toPojo;
    }

    @Override
    public Optional<P> fetchOptional()
    {
        return fetchRecordOptional().map(toPojo);
    }

    @Override
    public Optional<R> fetchRecordOptional()
    {
        return doFetch(ResultQuery::fetchOptional);
    }

    private <T> T doFetch(Function<ResultQuery<R>, T> method)
    {
        ResultQuery<R> resultQuery = resultQuerySupplier.get();
        resultQuery.attach(context.configuration());
        try
        {
            return method.apply(resultQuery);
        }
        catch (DataAccessException e)
        {
            throw new StoreLayerException("Error fetching " + getRecordClassName(resultQuery), e);
        }
    }

    private String getRecordClassName(ResultQuery<R> resultQuery)
    {
        return resultQuery.getRecordType()
            .getSimpleName();
    }

    @Override
    public Stream<R> fetchRecordStream()
    {
        return doFetch(ResultQuery::stream);
    }

    @Override
    public Stream<P> fetchStream()
    {
        return fetchRecordStream().map(toPojo);
    }

    @Override
    public void filter(Condition condition)
    {
        addCondition(condition);
    }

    private void addCondition(Condition condition)
    {
        this.condition = this.condition.and(condition);
    }

    @Override
    public void findWhere(Condition condition)
    {
        addCondition(condition);
    }

    @Override
    public void list()
    {
        // No-op; this method only exists for chain branching
    }

    @Override
    public void orderBy(OrderField<?>... field)
    {
        this.orderByFields = field;
    }

    @Override
    public void query(ResultQuery<R> resultQuery)
    {
        resultQuerySupplier = () -> resultQuery;
    }

    @Override
    public void read(Identifier<P> id)
    {
        addCondition(getIdField().eq(id.getValue()));
    }

    @SuppressWarnings("unchecked")
    private TableField<R, String> getIdField()
    {
        var primaryKeyFields = table.getPrimaryKey()
            .getFields();
        if (primaryKeyFields.size() > 1)
        {
            throw new IllegalStateException("Table " + table.getUnqualifiedName() + " has a multi-column primary key");
        }

        var idField = primaryKeyFields.get(0);
        if (!idField.getDataType()
            .isString())
        {
            throw new IllegalStateException("Table " + table.getUnqualifiedName() + " has a non-string primary key");
        }
        return (TableField<R, String>) idField;
    }

    @Override
    public void selectFrom(Table<R> table)
    {
        this.table = table;
        resultQuerySupplier = this::buildResultQuery;
    }

    private ResultQuery<R> buildResultQuery()
    {
        return DSL.selectFrom(table)
            .where(condition)
            .orderBy(orderByFields);
    }

    @Override
    public void skipConversion()
    {
        // No-op; this method only exists for chain branching
    }
}
