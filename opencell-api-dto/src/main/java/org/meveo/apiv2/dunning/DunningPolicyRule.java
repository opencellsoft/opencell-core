package org.meveo.apiv2.dunning;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDunningPolicyRule.class)
public interface DunningPolicyRule extends Resource {

    @Schema(description = "Rule joint")
    @Nullable
    String getRuleJoint();

    @Schema(description = "Dunning policy")
    @Nullable
    Resource getDunningPolicy();
}