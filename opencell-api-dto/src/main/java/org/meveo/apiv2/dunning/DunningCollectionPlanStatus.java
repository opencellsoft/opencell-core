package org.meveo.apiv2.dunning;

import javax.annotation.Nonnull;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.payments.DunningCollectionPlanStatusEnum;

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
    DunningCollectionPlanStatusEnum getStatus();

    @Schema(description = "indicate description for the collection")
    @Nonnull
    String getDescription();

    @Schema(description = "indicate color code for the status")
    String getColorCode();
}
