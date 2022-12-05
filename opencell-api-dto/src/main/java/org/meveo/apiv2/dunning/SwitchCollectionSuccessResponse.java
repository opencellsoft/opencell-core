package org.meveo.apiv2.dunning;

import jakarta.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableSwitchCollectionSuccessResponse.class)
public interface SwitchCollectionSuccessResponse {

    @Nullable
    String getStatus();

    @Nullable
    DunningCollectionPlan getNewCollectionPlan();
}