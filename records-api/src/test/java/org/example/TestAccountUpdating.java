package org.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.example.Tables.ACCOUNT;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import org.example.business.Account;
import org.example.business.SingleSignOnPrincipal;
import org.example.store.AccountCreateStore;
import org.example.store.AccountUpdateStore;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.mizool.core.Identifier;
import com.github.mizool.core.exception.ConflictingEntityException;
import com.github.mizool.core.exception.InvalidPrimaryKeyException;
import com.github.mizool.core.exception.NotYetImplementedException;
import com.github.mizool.core.exception.ReadonlyFieldException;

@Test(dependsOnGroups = "AccountCreation")
public class TestAccountUpdating extends AbstractRecordsApiTest
{
    private AccountUpdateStore accountUpdateStore;
    private Account existingPojo;

    @BeforeMethod
    public void setUp()
    {
        super.setUp();

        accountUpdateStore = new AccountUpdateStore(accountRecordConverter, records);

        AccountCreateStore accountCreateStore = new AccountCreateStore(accountRecordConverter, records);
        existingPojo = accountCreateStore.create(Account.builder()
            .email("someone@example.org")
            .displayName("Someone")
            .ssoId(null)
            .build());
    }

    @AfterMethod
    public void tearDown()
    {
        context.deleteFrom(ACCOUNT)
            .where(ACCOUNT.ID.eq(existingPojo.getId()
                .getValue()))
            .execute();

        super.tearDown();
    }

    @Test
    public void testUpdateWithNewPojo()
    {
        Account newPojo = existingPojo.toBuilder()
            .displayName("Someone Important")
            .build();

        Account result = accountUpdateStore.update(newPojo);

        assertResultPojoHasFreshTimestamp(result, newPojo);

        assertThat(result).usingRecursiveComparison()
            .ignoringFieldsOfTypes(OffsetDateTime.class)
            .isEqualTo(newPojo);

        assertPersistedEntityMatches(result);
    }

    private void assertResultPojoHasFreshTimestamp(Account result, Account original)
    {
        assertThat(result.getTimestamp()).isAfter(original.getTimestamp());
    }

    private void assertPersistedEntityMatches(Account result)
    {
        Account persisted = selectDirectly(result.getId());
        assertThat(persisted).usingRecursiveComparison()
            .isEqualTo(result);
    }

    @Test
    public void testUnchangedUpdateWithNewPojo()
    {
        Account result = accountUpdateStore.update(existingPojo);

        assertResultPojoHasFreshTimestamp(result, existingPojo);
    }

    @Test
    public void testUpdateWithNewAndExistingPojo()
    {
        Account newPojo = existingPojo.toBuilder()
            .displayName("Someone Important")
            .build();

        Account result = accountUpdateStore.update(newPojo, existingPojo);

        assertResultPojoHasFreshTimestamp(result, newPojo);
    }

    @Test
    public void testUnchangedUpdateWithNewAndExistingPojo()
    {
        Account newPojo = existingPojo.toBuilder()
            .build();

        Account result = accountUpdateStore.update(newPojo, existingPojo);

        assertResultPojoHasFreshTimestamp(result, newPojo);
    }

    @Test
    public void testMissingId()
    {
        Account newPojo = existingPojo.toBuilder()
            .id(null)
            .displayName("Someone Important")
            .build();

        assertThatThrownBy(() -> accountUpdateStore.update(newPojo)).isInstanceOf(InvalidPrimaryKeyException.class);

        assertPersistedEntityMatches(existingPojo);
    }

    @Test
    public void testMissingTimestamp()
    {
        Account newPojo = existingPojo.toBuilder()
            .displayName("Someone Important")
            .timestamp(null)
            .build();

        assertThatThrownBy(() -> accountUpdateStore.update(newPojo)).isInstanceOf(ConflictingEntityException.class);

        assertPersistedEntityMatches(existingPojo);
    }

    @Test
    public void testPostdetectTimestampCollision()
    {
        Account newPojo = existingPojo.toBuilder()
            .displayName("Someone Important")
            .timestamp(existingPojo.getTimestamp()
                .plus(3, ChronoUnit.MILLIS))
            .build();

        assertThatThrownBy(() -> accountUpdateStore.update(newPojo)).isInstanceOf(ConflictingEntityException.class);

        assertPersistedEntityMatches(existingPojo);
    }

    @Test(enabled = false) // TODO
    public void testPredetectTimestampCollision()
    {
        throw new NotYetImplementedException();
    }

    @Test(enabled = false) // TODO
    public void testPostdetectTimestampCollisionDespitePassedPredetection()
    {
        throw new NotYetImplementedException();
    }

    @Test
    public void testRejectionOfChangingReadonlyFieldFromNull()
    {
        Account newPojo = existingPojo.toBuilder()
            .ssoId(randomSsoId())
            .build();

        assertThatThrownBy(() -> accountUpdateStore.update(newPojo)).isInstanceOf(ReadonlyFieldException.class);

        assertPersistedEntityMatches(existingPojo);
    }

    @Test
    public void testRejectionOfChangingReadonlyField()
    {
        runWithExistingAccountHavingSsoId(() -> {
            Account newPojo = existingPojo.toBuilder()
                .ssoId(randomSsoId())
                .build();

            assertThatThrownBy(() -> accountUpdateStore.update(newPojo)).isInstanceOf(ReadonlyFieldException.class);

            assertPersistedEntityMatches(existingPojo);
        });
    }

    private void runWithExistingAccountHavingSsoId(Runnable runnable)
    {
        try
        {
            setAndPersistSsoId(randomSsoId());
            runnable.run();
        }
        finally
        {
            setAndPersistSsoId(null);
        }
    }

    private void setAndPersistSsoId(Identifier<SingleSignOnPrincipal> ssoId)
    {
        existingPojo = existingPojo.toBuilder()
            .ssoId(ssoId)
            .build();

        String ssoIdString = ssoId == null
            ? null
            : ssoId.getValue();

        context.update(ACCOUNT)
            .set(ACCOUNT.SSO_ID, ssoIdString)
            .where(ACCOUNT.ID.eq(existingPojo.getId()
                .getValue()))
            .execute();
    }

    private Identifier<SingleSignOnPrincipal> randomSsoId()
    {
        return Identifier.forPojo(SingleSignOnPrincipal.class)
            .random();
    }

    @Test
    public void testRejectionOfChangingReadonlyFieldToNull()
    {
        runWithExistingAccountHavingSsoId(() -> {
            Account newPojo = existingPojo.toBuilder()
                .ssoId(null)
                .build();

            assertThatThrownBy(() -> accountUpdateStore.update(newPojo)).isInstanceOf(ReadonlyFieldException.class);

            assertPersistedEntityMatches(existingPojo);
        });
    }
}
