package org.meveo.apiv2.dunning;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDunningCollectionPlanStatus.class)
public interface DunningCollectionPlanStatus extends Resource {

    @Schema(description = "dunning setting id")
    @Nonnull
    Resource getDunningSettings();

    @Schema(description = "indicate the status used in the collection")
    @Nonnull
    String getStatus();

    @Schema(description = "indicate description for the collection")
    @Nullable
    String getDescription();

    @Schema(description = "indicate color code for the status")
    String getColorCode();
}
