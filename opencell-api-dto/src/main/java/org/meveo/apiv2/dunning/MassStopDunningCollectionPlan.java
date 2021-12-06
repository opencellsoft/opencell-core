package org.meveo.apiv2.dunning;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableMassStopDunningCollectionPlan.class)
public interface MassStopDunningCollectionPlan extends Resource {

    @Schema(description = "Indicate dunning list of collection plan")
    @Nullable
    List<Resource> getCollectionPlans();

    @Schema(description = "Indicate dunning stop reason")
    @Nullable
    Resource getDunningStopReason();
}