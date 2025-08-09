package dev.bannmann.labs.anansi.postgres;

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import dev.bannmann.anansi.core.StorableFingerprint;
import dev.bannmann.labs.anansi.postgres.generated.tables.records.FingerprintRecord;
import dev.bannmann.labs.annotations.SuppressWarningsRationale;

@RequiredArgsConstructor(onConstructor_ = @Inject, access = AccessLevel.PROTECTED)
class FingerprintRecordConverter
{
    @SuppressWarnings("NullAway")
    @SuppressWarningsRationale("NullAway thinks FingerprintRecord setter parameters are not nullable")
    public FingerprintRecord fromPojo(StorableFingerprint pojo)
    {
        FingerprintRecord result = new FingerprintRecord();
        result.setId(pojo.getId());
        result.setName(pojo.getName());
        result.setLocation(pojo.getLocation());
        result.setFrames(pojo.getFrames());
        result.setExtraData(pojo.getExtraData());
        return result;
    }
}
