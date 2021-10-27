package org.meveo.apiv2.dunning;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDunningStopReason.class)
public interface DunningStopReason extends Resource {

    @Schema(description = "The stop reason")
    @Nonnull
    String getStopReason();

    @Schema(description = "The stop reason's description")
    @Nullable
    String getDescription();

    @Schema(description = "The dunning settings associated to stop reason")
    @Nonnull
	Resource getDunningSettings();
}
