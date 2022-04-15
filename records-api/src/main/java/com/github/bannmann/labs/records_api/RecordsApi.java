package com.github.bannmann.labs.records_api;

import java.time.OffsetDateTime;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import lombok.experimental.UtilityClass;

import org.jooq.Condition;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UpdatableRecord;

import com.github.mizool.core.Identifiable;
import com.github.mizool.core.Identifier;

@UtilityClass
public class RecordsApi
{
    public interface EntryPoints
    {
        <R extends UpdatableRecord<R>> InsertStart<R> insertInto(Table<R> table);

        <R extends UpdatableRecord<R>> UpdateStart<R> update(Table<R> table);

        // TODO support deletion once Charlie has delete stores
    }

    public interface InsertStart<R extends UpdatableRecord<R>>
    {
        <P extends Identifiable<P>> InsertSetPojo<R, P> withIdentifiableConvertedVia(Function<P, R> fromPojo);

        <P> InsertSetPojo<R, P> withAnonymousConvertedVia(Function<P, R> fromPojo);
    }

    public interface InsertSetPojo<R extends UpdatableRecord<R>, P>
    {
        InsertMain<R, P> fromPojo(P newPojo);
    }

    public interface InsertMain<R extends UpdatableRecord<R>, P> extends SharedFinish<R, P, InsertMain<R, P>>
    {
        InsertMain<R, P> generating(TableField<R, OffsetDateTime> field);
    }

    public interface SharedFinish<R extends UpdatableRecord<R>, P, C>
    {
        <F> C adjusting(TableField<R, F> field, UnaryOperator<F> adjuster);

        C normalizingEmail(TableField<R, String> field);

        P executeAndConvert(Function<R, P> convertToPojo);
    }

    public interface UpdateStart<R extends UpdatableRecord<R>>
    {
        /**
         * Supports both {@link Identifiable} and "anonymous" pojos.
         */
        <P> UpdateSetPojo<R, P> withRecordConvertedVia(Function<P, R> fromPojo);

        /**
         * @param id the identifier to use as primary key
         * @param identifiableClass required for catching Identifier mixups at compile time
         */
        <I extends Identifiable<I>> PartialUpdate<R> withPrimaryKey(Identifier<I> id, Class<I> identifiableClass);
    }

    public interface PartialUpdateAssignments<R extends UpdatableRecord<R>, C>
    {
        /**
         * @param value may be {@code null}
         */
        <F> C set(TableField<R, F> field, F value);

        <F> C increase(TableField<R, F> field);

        C refresh(TableField<R, OffsetDateTime> timestampField);
    }

    public interface PartialUpdate<R extends UpdatableRecord<R>>
        extends PartialUpdateAssignments<R, PartialUpdatesRegistered<R>>
    {
    }

    public interface PartialUpdatesRegistered<R extends UpdatableRecord<R>>
        extends PartialUpdate<R>, PartialUpdateFinish<R>
    {
    }

    public interface PartialUpdateFinish<R extends UpdatableRecord<R>> extends PostDetect<R, PartialUpdateFinish<R>>
    {
        void execute();
    }

    public interface PostDetect<R extends UpdatableRecord<R>, C>
    {
        /**
         * Enables collision detection based on the given condition. The negated version of the condition is used in the
         * {@code WHERE} clause together with the primary key. If no rows are updated, another database roundtrip is
         * made to distinguish the 'collision' from 'invalid PK' case.
         *
         * @param condition the condition which evaluates to true if a collision occurred
         * @param name a string to include in the exception message on failure. For simple one-field conditions, use
         * {@link #postdetectCollisionIf(Condition, TableField)} instead.
         */
        C postdetectCollisionIf(Condition condition, String name);

        /**
         * Enables collision detection based on the given condition. The negated version of the condition is used in the
         * {@code WHERE} clause together with the primary key. If no rows are updated, another database roundtrip is
         * made to distinguish the 'collision' from 'invalid PK' case.
         *
         * @param condition the condition which evaluates to true if a collision occurred
         * @param field the field checked by the condition (included in the exception message on failure). For
         * multi-field conditions, use {@link #postdetectCollisionIf(Condition, String)} instead.
         */
        C postdetectCollisionIf(Condition condition, TableField<R, ?> field);
    }

    public interface UpdatesRegistered<R extends UpdatableRecord<R>, P>
        extends PostDetect<R, UpdatesRegistered<R, P>>, SharedFinish<R, P, UpdatesRegistered<R, P>>
    {
    }

    public interface FieldChecks<R extends UpdatableRecord<R>, P, C> extends UpdatesRegistered<R, P>
    {
        /**
         * Verifies that the update will not change the value of the given field. <br>
         * <br>
         * By default, adds a {@code WHERE} condition similar to {@linkplain UpdatesRegistered#postdetectCollisionIf(Condition,
         * TableField) postdetect collision checks}. In addition, if an existing pojo has been given, it is used for an
         * additional comparison before contacting the database.<br>
         * <br>
         * Updates which attempt to change the field value will result in an {@link ReadonlyFieldException} stating that
         * the field is readonly.
         *
         * @param field the field to check
         */
        C verifyUnchanged(TableField<R, ?> field);

        /**
         * Enables collision detection for the given field (based on the pojo's value), then refreshes it. <br>
         * <br>
         * Triggers a {@linkplain UpdatesRegistered#postdetectCollisionIf(Condition, TableField) postdetect} collision
         * check by default. If an existing pojo has been given, also makes an additional {@linkplain
         * FullUpdateWithExisting#predetectCollisionOn(TableField) predetect} collision check.<br>
         * <br>
         * Updates without a matching value will result in a {@link com.github.mizool.core.exception.ConflictingEntityException}.
         *
         * @param field the field to use for collision detection and to refresh
         */
        C checkAndRefresh(TableField<R, OffsetDateTime> field);

        /**
         * Enables collision detection for the given field (based on the pojo's value), then increases it. <br>
         * <br>
         * Triggers a {@linkplain UpdatesRegistered#postdetectCollisionIf(Condition, TableField) postdetect} collision
         * check by default. If an existing pojo has been given, also makes an additional {@linkplain
         * FullUpdateWithExisting#predetectCollisionOn(TableField) predetect} collision check.<br>
         * <br>
         * Updates without a matching value will result in a {@link com.github.mizool.core.exception.ConflictingEntityException}.
         *
         * @param field the field to use for collision detection and to increase
         */
        C checkAndIncrease(TableField<R, Integer> field);

        /**
         * Enables collision detection for the given field (based on the pojo's value), then randomizes it. <br>
         * <br>
         * Triggers a {@linkplain UpdatesRegistered#postdetectCollisionIf(Condition, TableField) postdetect} collision
         * check by default. If an existing pojo has been given, also makes an additional {@linkplain
         * FullUpdateWithExisting#predetectCollisionOn(TableField) predetect} collision check.<br>
         * <br>
         * Updates without a matching value will result in a {@link com.github.mizool.core.exception.ConflictingEntityException}.
         *
         * @param field the field to use for collision detection and to randomize
         * @param randomSupplier the supplier for the new random value to set
         */
        <U> C checkAndRandomize(TableField<R, U> field, Supplier<U> randomSupplier);
    }

    public interface UpdateSetPojo<R extends UpdatableRecord<R>, P>
    {
        FullUpdateStart<R, P> fromNewPojo(P newPojo);
    }

    public interface FullUpdateStart<R extends UpdatableRecord<R>, P> extends FullUpdate<R, P>
    {
        /**
         * Registers the existing pojo for use by immutability checks (built-in) and collision detection (enabled
         * separately).
         */
        FullUpdateWithExisting<R, P> andExistingPojo(P existingPojo);
    }

    public interface FullUpdate<R extends UpdatableRecord<R>, P> extends FieldChecks<R, P, FullUpdate<R, P>>
    {
    }

    public interface FullUpdateWithExisting<R extends UpdatableRecord<R>, P>
        extends FieldChecks<R, P, FullUpdateWithExisting<R, P>>
    {
        /**
         * Enables collision detection by comparing the values of the given field in the given new and existing records
         * before contacting the database. Also enables {@linkplain UpdatesRegistered#postdetectCollisionIf(Condition,
         * TableField) late collision detection (postdetect)}.
         */
        <V> FullUpdateWithExisting<R, P> predetectCollisionOn(TableField<R, V> field);
    }
}
