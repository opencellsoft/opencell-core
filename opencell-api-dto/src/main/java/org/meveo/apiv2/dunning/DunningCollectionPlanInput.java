package org.meveo.apiv2.dunning;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDunningCollectionPlanInput.class)
public interface DunningCollectionPlanInput extends Resource {

    @Schema(description = "Indicate dunning policy")
    @Nullable
    Resource getDunningPolicy();

    @Schema(description = "Indicate policy level")
    @Nullable
    Resource getPolicyLevel();
}