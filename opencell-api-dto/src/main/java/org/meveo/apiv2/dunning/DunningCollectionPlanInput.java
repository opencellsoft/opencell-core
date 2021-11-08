package org.meveo.apiv2.dunning;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;
import java.util.Date;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDunningCollectionPlanInput.class)
public interface DunningCollectionPlanInput extends Resource {

    @Schema(description = "Indicate collection plan stop date")
    @Nullable
    Date getStopDate();

    @Schema(description = "Indicate collection plan stop reason")
    @Nullable
    Resource getStopReason();

    @Schema(description = "Indicate policy level")
    @Nullable
    Resource getPolicyLevel();
}