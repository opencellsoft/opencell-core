package org.meveo.apiv2.dunning;

import java.util.Date;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDunningCollectionPlanPause.class)
public interface DunningCollectionPlanPause extends Resource {

	@Schema(description = "The pause reason")
    Resource getDunningPauseReason();
    
    @Schema(description = "Force pause")
    boolean getForcePause();
    
    @Schema(description = "Pause until date")
	Date getPauseUntil();
    
    
}
