package com.github.bannmann.labs.records_api.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.generated.Tables.ACCOUNT;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.bannmann.labs.records_api.manual.Records;

public class TestManualRecordsApi extends AbstractRecordsApiTest
{
    private Records records;

    @BeforeClass
    public void setUp()
    {
        super.setUp();
        records = new Records(context, storeClock);
    }

    @Test
    public void test()
    {
        Account pojo = Account.builder()
            .email("John.Doe@EXAMPLE.ORG")
            .build();

        Account result = records.insertInto(ACCOUNT)
            .withIdentifiableConvertedVia(accountRecordConverter::fromPojo)
            .fromPojo(pojo)
            .generating(ACCOUNT.TIMESTAMP)
            .normalizingEmail(ACCOUNT.EMAIL)
            .executeAndConvert(accountRecordConverter::toPojo);

        assertThat(result).hasNoNullFieldsOrProperties();

        Account persisted = readFromDatabase(result.getId());
        assertThat(persisted).isEqualTo(result);
    }
}
