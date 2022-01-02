package org.example;

import static org.example.Tables.ACCOUNT;

import org.example.business.Account;
import org.example.store.AccountRecordConverter;
import org.example.tables.records.AccountRecord;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.github.bannmann.labs.records_api.Records;
import com.github.bannmann.labs.records_api.StoreClock;
import com.github.mizool.core.Identifier;
import com.github.mizool.core.converter.IdentifierConverter;

abstract class AbstractRecordsApiTest
{
    protected final IdentifierConverter identifierConverter = new IdentifierConverter();
    protected final AccountRecordConverter accountRecordConverter = new AccountRecordConverter(identifierConverter);

    protected DSLContext context;
    protected Records records;

    @BeforeClass
    public void setUp()
    {
        context = DSL.using(System.getProperty("jdbcUrl"));
        StoreClock storeClock = new StoreClock();
        records = new Records(context, storeClock);
    }

    @AfterClass
    public void tearDown()
    {
        context.close();
    }

    protected Account selectDirectly(Identifier<Account> id)
    {
        return context.selectFrom(ACCOUNT)
            .where(ACCOUNT.ID.eq(id.getValue()))
            .fetchOne(accountRecordConverter::toPojo);
    }
}
