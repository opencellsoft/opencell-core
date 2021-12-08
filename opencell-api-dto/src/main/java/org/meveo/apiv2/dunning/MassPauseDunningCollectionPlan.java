package org.meveo.apiv2.dunning;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;

import java.util.Date;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableMassPauseDunningCollectionPlan.class)
public interface MassPauseDunningCollectionPlan {

    @Schema(description = "Collection plan list to check")
    List<Resource> getCollectionPlans();

	@Schema(description = "The pause reason")
    Resource getDunningPauseReason();
    
    @Schema(description = "Force pause")
    boolean getForcePause();
    
    @Schema(description = "Pause until date")
	Date getPauseUntil();

}