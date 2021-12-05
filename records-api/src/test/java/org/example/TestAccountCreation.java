package org.example;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.UnaryOperator;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.github.bannmann.labs.records_api.manual.Records;

public class TestAccountCreation extends AbstractRecordsApiTest
{
    private Records manualRecordsApi;

    @BeforeClass
    public void setUp()
    {
        super.setUp();
        manualRecordsApi = new Records(context, storeClock);
    }

    @Test(dataProvider = "stores")
    public void test(@SuppressWarnings("unused") String testName, UnaryOperator<Account> createStoreCall)
    {
        Account pojo = Account.builder()
            .email("John.Doe@EXAMPLE.ORG")
            .build();

        Account result = createStoreCall.apply(pojo);

        assertThat(result).hasNoNullFieldsOrProperties();

        Account persisted = readFromDatabase(result.getId());
        assertThat(persisted).isEqualTo(result);
    }

    @DataProvider
    private Object[][] stores()
    {
        return new Object[][]{
            testdata("manual",
                account -> new ManualRecordsApiVersionOfAccountCreateStore(accountRecordConverter,
                    manualRecordsApi).create(account))
        };
    }

    private Object[] testdata(String testName, UnaryOperator<Account> storeCall)
    {
        return new Object[]{
            testName, storeCall
        };
    }
}
