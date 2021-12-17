package org.meveo.apiv2.dunning;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDunningCollectionPlanStop.class)
public interface DunningCollectionPlanStop extends Resource {

    @Schema(description = "The stop reason")
    Resource getDunningStopReason();
    
}
