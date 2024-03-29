package org.example;

import static org.example.Tables.ACCOUNT;

import org.example.business.Account;
import org.example.store.AccountRecordConverter;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.testng.annotations.BeforeMethod;

import com.github.mizool.core.Identifier;
import com.github.mizool.core.converter.IdentifierConverter;
import dev.bannmann.labs.records_api.Records;
import dev.bannmann.labs.records_api.StoreClock;

abstract class AbstractRecordsApiTest
{
    protected final IdentifierConverter identifierConverter = new IdentifierConverter();
    protected final AccountRecordConverter accountRecordConverter = new AccountRecordConverter(identifierConverter);

    protected DSLContext context;
    protected StoreClock storeClock;
    protected Records records;

    @BeforeMethod
    public void setUp()
    {
        context = DSL.using(System.getProperty("jdbcUrl"));
        storeClock = new StoreClock();
        records = new Records(context, storeClock);
    }

    protected Account selectAccountDirectly(Identifier<Account> id)
    {
        return context.selectFrom(ACCOUNT)
            .where(ACCOUNT.ID.eq(id.getValue()))
            .fetchOne(accountRecordConverter::toPojo);
    }
}
