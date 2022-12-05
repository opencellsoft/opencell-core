package org.meveo.apiv2.dunning;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDunningPauseReason.class)
public interface DunningPauseReason extends Resource {

    @Schema(description = "The pause reason")
    @Nonnull
    String getPauseReason();

    @Schema(description = "The pause reason's description")
    @Nullable
    String getDescription();

    @Schema(description = "The dunning settings associated to pause reason")
    @Nonnull
	Resource getDunningSettings();
}
