package org.meveo.apiv2.dunning;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDunningPolicyRuleLine.class)
public interface DunningPolicyRuleLine extends Resource {

    @Schema(description = "Policy condition operation")
    @Nullable
    String getPolicyConditionOperator();

    @Schema(description = "Policy condition target")
    @Nullable
    String getPolicyConditionTarget();

    @Schema(description = "Policy condition target value")
    @Nullable
    String getPolicyConditionTargetValue();

    @Schema(description = "Rule line joint")
    @Nullable
    String getRuleLineJoint();
}