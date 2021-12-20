package org.meveo.apiv2.dunning;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableMassOperationSuccessResponse.class)
public interface MassOperationSuccessResponse {
    @Nullable
    String getStatus();
}