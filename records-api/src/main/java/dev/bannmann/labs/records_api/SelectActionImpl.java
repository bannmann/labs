package dev.bannmann.labs.records_api;

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
import org.jspecify.annotations.NullMarked;

import com.github.mizool.core.Identifiable;
import com.github.mizool.core.Identifier;
import com.github.mizool.core.exception.StoreLayerException;
import dev.bannmann.labs.annotations.SuppressWarningsRationale;
import dev.bannmann.labs.core.Box;

@Slf4j
@RequiredArgsConstructor
@NullMarked
@SuppressWarnings("java:S2638")
@SuppressWarningsRationale(
    "The Silverchain-generated interface is not @NullMarked yet, so Sonar flags our use of lombok @NonNull on many parameters as a contract change.")
public class SelectActionImpl<P extends Identifiable<P>, R extends Record> implements ISelectAction<P, R>
{
    private final Box<Supplier<ResultQuery<R>>> resultQuerySupplierBox = new Box<>();
    private final Box<Function<R, P>> toPojoBox = new Box<>();
    private final Box<Table<R>> tableBox = new Box<>();

    private final DSLContext context;

    private Condition condition = DSL.noCondition();
    private OrderField<?>[] orderByFields = new OrderField[]{};

    @Override
    public void convertedUsing(RecordConverter<P, R> converter)
    {
        convertedVia(converter::toPojo);
    }

    @Override
    public void convertedVia(Function<R, P> toPojo)
    {
        toPojoBox.set(toPojo);
    }

    @Override
    public Optional<P> fetchOptional()
    {
        return fetchRecordOptional().map(toPojoBox.get());
    }

    @Override
    public Optional<R> fetchRecordOptional()
    {
        return doFetch(ResultQuery::fetchOptional);
    }

    private <T> T doFetch(Function<ResultQuery<R>, T> method)
    {
        var resultQuerySupplier = resultQuerySupplierBox.get();
        var resultQuery = resultQuerySupplier.get();
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
        return fetchRecordStream().map(toPojoBox.get());
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
        orderByFields = field;
    }

    @Override
    public void query(ResultQuery<R> resultQuery)
    {
        resultQuerySupplierBox.set(() -> resultQuery);
    }

    @Override
    public void read(Identifier<P> id)
    {
        addCondition(getIdField().eq(id.getValue()));
    }

    private TableField<R, String> getIdField()
    {
        return Tables.obtainSingleStringPrimaryKeyField(tableBox.get());
    }

    @Override
    public void selectFrom(Table<R> table)
    {
        tableBox.set(table);
        resultQuerySupplierBox.set(this::buildResultQuery);
    }

    private ResultQuery<R> buildResultQuery()
    {
        return DSL.selectFrom(tableBox.get())
            .where(condition)
            .orderBy(orderByFields);
    }

    @Override
    public void skipConversion()
    {
        // No-op; this method only exists for chain branching
    }
}
