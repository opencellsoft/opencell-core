package org.meveo.apiv2.dunning;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDunningPolicyLevel.class)
public interface DunningPolicyLevel extends Resource {

    @Schema(description = "Dunning level id")
    @Nullable
    Long getDunningLevelId();

    @Schema(description = "Collection plan status Id")
    @Nullable
    Long getCollectionPlanStatusId();
}