package com.github.bannmann.labs.records_api;

import java.util.function.BiFunction;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import com.github.mizool.core.exception.ConflictingEntityException;
import com.github.mizool.core.exception.ReadonlyFieldException;

@RequiredArgsConstructor
@Getter
enum CheckReason
{
    COLLISION_DETECTION((recordKey, checkLabel) -> new ConflictingEntityException(String.format(
        "%s is in a conflicting state (%s)",
        recordKey,
        checkLabel))),
    VERIFY_UNCHANGED((recordKey, checkLabel) -> new ReadonlyFieldException(checkLabel.toString(),
        recordKey.toString()));

    @NonNull
    private final BiFunction<RecordKey, CheckLabel, RuntimeException> exceptionBuilder;
}
