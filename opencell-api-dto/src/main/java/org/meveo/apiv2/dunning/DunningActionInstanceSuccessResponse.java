package org.meveo.apiv2.dunning;

import jakarta.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDunningActionInstanceSuccessResponse.class)
public interface DunningActionInstanceSuccessResponse {

    @Nullable
    String getStatus();

    @Nullable
    DunningActionInstanceInput getNewDunningActionInstance();
}