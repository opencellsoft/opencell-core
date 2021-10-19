package org.meveo.apiv2.dunning;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDunningPauseReasons.class)
public interface DunningPauseReasons extends Resource {

    @Schema(description = "The language code")
    @Nullable
	String getLanguage();

    @Schema(description = "The pause reason")
    @Nullable
    String getPauseReason();

    @Schema(description = "The pause reason's description")
    @Nullable
    String getDescription();

    @Schema(description = "The dunning settings associated to pause reason")
    @Nonnull
	Resource getDunningSetting();
}
