package org.meveo.apiv2.dunning;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDunningAgentInput.class)
public interface DunningAgentInput extends Resource {

    @Schema(description = "indicate dunning settings")
    @Nullable
    Resource getDunningSettings();

    @Schema(description = "include collection agency")
    boolean getExternal();

    @Schema(description = "indicate email collection agency")
    @Nullable
    String getCollectionAgency();

    @Schema(description = "")
    @Nullable
    String getAgentFirstNameItem();

    @Schema(description = "")
    @Nullable
    String getAgentLastNameItem();

    @Schema(description = "")
    @Nullable
    String getAgentEmailItem();
}
