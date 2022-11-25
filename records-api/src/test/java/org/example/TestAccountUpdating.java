package org.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.example.Tables.ACCOUNT;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import org.assertj.core.api.Assertions;
import org.example.business.Account;
import org.example.business.SingleSignOnPrincipal;
import org.example.store.AccountCreateStore;
import org.example.store.AccountUpdateStore;
import org.jooq.DSLContext;
import org.mockito.Mockito;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.bannmann.labs.records_api.Records;
import com.github.mizool.core.Identifier;
import com.github.mizool.core.exception.ConflictingEntityException;
import com.github.mizool.core.exception.InvalidPrimaryKeyException;
import com.github.mizool.core.exception.ReadonlyFieldException;

@Test
public class TestAccountUpdating extends AbstractRecordsApiTest
{
    private AccountUpdateStore accountUpdateStore;
    private Account existingAccount;
    private Account anotherAccount;
    private DSLContext contextUnderTest;
    private Records recordsUnderTest;

    @BeforeMethod
    public void setUp()
    {
        super.setUp();

        contextUnderTest = Mockito.spy(context);
        recordsUnderTest = new Records(contextUnderTest, storeClock);
        accountUpdateStore = new AccountUpdateStore(accountRecordConverter, recordsUnderTest);

        Assertions.setMaxStackTraceElementsDisplayed(999);

        context.deleteFrom(ACCOUNT)
            .execute();

        AccountCreateStore accountCreateStore = new AccountCreateStore(accountRecordConverter, records);
        existingAccount = accountCreateStore.create(Account.builder()
            .email("someone@example.org")
            .displayName("Someone")
            .ssoId(null)
            .build());
        anotherAccount = accountCreateStore.create(Account.builder()
            .email("ceo@example.com")
            .displayName("John J. Ferguson")
            .ssoId(null)
            .build());
    }

    @AfterMethod
    public void tearDown()
    {
        context.deleteFrom(ACCOUNT)
            .execute();

        super.tearDown();
    }

    @Test
    public void testUpdateWithNewPojo()
    {
        Account newPojo = existingAccount.toBuilder()
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
        Account persisted = selectAccountDirectly(result.getId());
        assertThat(persisted).usingRecursiveComparison()
            .isEqualTo(result);
    }

    @Test
    public void testUnchangedUpdateWithNewPojo()
    {
        Account result = accountUpdateStore.update(existingAccount);

        assertResultPojoHasFreshTimestamp(result, existingAccount);
    }

    @Test
    public void testUpdateWithNewAndExistingPojo()
    {
        Account newPojo = existingAccount.toBuilder()
            .displayName("Someone Important")
            .build();

        Account result = accountUpdateStore.update(newPojo, existingAccount);

        assertResultPojoHasFreshTimestamp(result, newPojo);
    }

    @Test
    public void testUnchangedUpdateWithNewAndExistingPojo()
    {
        Account newPojo = existingAccount.toBuilder()
            .build();

        Account result = accountUpdateStore.update(newPojo, existingAccount);

        assertResultPojoHasFreshTimestamp(result, newPojo);
    }

    @Test
    public void testMissingId()
    {
        Account newPojo = existingAccount.toBuilder()
            .id(null)
            .displayName("Someone Important")
            .build();

        assertThatThrownBy(() -> accountUpdateStore.update(newPojo)).isInstanceOf(InvalidPrimaryKeyException.class);

        assertPersistedEntityMatches(existingAccount);
    }

    @Test
    public void testMissingTimestamp()
    {
        Account newPojo = existingAccount.toBuilder()
            .displayName("Someone Important")
            .timestamp(null)
            .build();

        assertThatThrownBy(() -> accountUpdateStore.update(newPojo)).isInstanceOf(ConflictingEntityException.class);

        assertPersistedEntityMatches(existingAccount);
    }

    @Test
    public void testPostdetectTimestampCollision()
    {
        Account newPojo = existingAccount.toBuilder()
            .displayName("Someone Important")
            .timestamp(existingAccount.getTimestamp()
                .plus(3, ChronoUnit.MILLIS))
            .build();

        assertThatThrownBy(() -> accountUpdateStore.update(newPojo)).isInstanceOf(ConflictingEntityException.class);

        assertPersistedEntityMatches(existingAccount);
    }

    @Test
    public void testPredetectTimestampCollision()
    {
        Account newPojo = existingAccount.toBuilder()
            .displayName("Someone Important")
            .timestamp(existingAccount.getTimestamp()
                .plus(3, ChronoUnit.MILLIS))
            .build();

        assertThatThrownBy(() -> accountUpdateStore.update(newPojo, existingAccount)).isInstanceOf(
            ConflictingEntityException.class);

        // Verify that the collision was detected without contacting the database
        verifyNoInteractions(contextUnderTest);
    }

    @Test
    public void testPostdetectTimestampCollisionDespitePassedPredetection()
    {
        /*
         * Scenario:
         * Clients A and B submits a new version of a pojo. Each includes the valid original timestamp (8 hours ago).
         *
         * Chronology:
         * 1) Thread for client A reads the existing account (timestamp = 8 hours ago)
         * 2) Thread for client B updates the row (setting timestamp to 'now')
         * 3) Thread for client A triggers an update, passing both existing and new pojo
         *
         * Results:
         * The predetect check (before contacting the database) in step 3 succeeds as the timestamp of both pojos match,
         * but the postdetect check (WHERE condition) reveals the collision.
         */

        Account baseVersion = existingAccount.toBuilder()
            .timestamp(existingAccount.getTimestamp()
                .minus(8, ChronoUnit.HOURS))
            .build();

        Account newPojo = baseVersion.toBuilder()
            .displayName("Someone Important")
            .build();

        // Note: neither baseVersion nor newPojo match the database row (which equals existingPojo).

        assertThatThrownBy(() -> accountUpdateStore.update(newPojo, baseVersion)).isInstanceOf(
            ConflictingEntityException.class);

        assertPersistedEntityMatches(existingAccount);
    }

    @Test
    public void testRejectionOfChangingReadonlyFieldFromNull()
    {
        Account newPojo = existingAccount.toBuilder()
            .ssoId(randomSsoId())
            .build();

        assertThatThrownBy(() -> accountUpdateStore.update(newPojo)).isInstanceOf(ReadonlyFieldException.class);

        assertPersistedEntityMatches(existingAccount);
    }

    @Test
    public void testRejectionOfChangingReadonlyField()
    {
        runWithExistingAccountHavingSsoId(() -> {
            Account newPojo = existingAccount.toBuilder()
                .ssoId(randomSsoId())
                .build();

            assertThatThrownBy(() -> accountUpdateStore.update(newPojo)).isInstanceOf(ReadonlyFieldException.class);

            assertPersistedEntityMatches(existingAccount);
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
        existingAccount = existingAccount.toBuilder()
            .ssoId(ssoId)
            .build();

        String ssoIdString = ssoId == null
            ? null
            : ssoId.getValue();

        context.update(ACCOUNT)
            .set(ACCOUNT.SSO_ID, ssoIdString)
            .where(ACCOUNT.ID.eq(existingAccount.getId()
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
            Account newPojo = existingAccount.toBuilder()
                .ssoId(null)
                .build();

            assertThatThrownBy(() -> accountUpdateStore.update(newPojo)).isInstanceOf(ReadonlyFieldException.class);

            assertPersistedEntityMatches(existingAccount);
        });
    }

    @Test
    public void testUniqueConstraintViolation()
    {
        Account newPojo = existingAccount.toBuilder()
            .displayName(anotherAccount.getDisplayName())
            .build();

        assertThatThrownBy(() -> accountUpdateStore.update(newPojo)).isInstanceOf(ConflictingEntityException.class)
            .hasMessage("Conflict with existing entity due to DISPLAY_NAME");
    }
}
