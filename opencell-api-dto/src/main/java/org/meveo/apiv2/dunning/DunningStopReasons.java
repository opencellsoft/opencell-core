package org.meveo.apiv2.dunning;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.dunning.DunningModeEnum;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDunningStopReasons.class)
public interface DunningStopReasons extends Resource {

    @Schema(description = "The language code")
    @Nullable
	String getLanguage();

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
