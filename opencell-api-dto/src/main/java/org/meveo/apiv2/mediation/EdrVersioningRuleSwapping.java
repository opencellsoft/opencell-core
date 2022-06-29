package org.meveo.apiv2.mediation;

import javax.validation.constraints.NotNull;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableEdrVersioningRuleSwapping.class)
public interface EdrVersioningRuleSwapping extends Resource {


    @Schema(description = "rule one to swap")
    @NotNull
	Resource getRule1();
    


    @Schema(description = "rule two to swap")
    @NotNull
	Resource getRule2();
}
