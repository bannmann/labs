package org.example;

import static org.example.Tables.ACCOUNT;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.github.bannmann.labs.records_api.StoreClock;
import com.github.mizool.core.Identifier;
import com.github.mizool.core.converter.IdentifierConverter;

abstract class AbstractRecordsApiTest
{
    protected final IdentifierConverter identifierConverter = new IdentifierConverter();
    protected final AccountRecordConverter accountRecordConverter = new AccountRecordConverter(identifierConverter);

    protected DSLContext context;
    protected StoreClock storeClock;

    @BeforeClass
    public void setUp()
    {
        context = DSL.using(System.getProperty("jdbcUrl"));
        storeClock = new StoreClock();
    }

    @AfterClass
    public void tearDown()
    {
        context.close();
    }

    protected Account readFromDatabase(Identifier<Account> id)
    {
        return context.selectFrom(ACCOUNT)
            .where(ACCOUNT.ID.eq(id.getValue()))
            .fetchOne(accountRecordConverter::toPojo);
    }
}
