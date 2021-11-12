package org.meveo.apiv2.dunning;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutablePolicyRule.class)
public interface PolicyRule extends Resource {

    @Schema(description = "Policy rule's rule join")
    @Nullable
    String getRuleJoint();

    @Schema(description = "List of policy rule lines")
    @Nullable
    List<DunningPolicyRuleLine> getRuleLines();
}