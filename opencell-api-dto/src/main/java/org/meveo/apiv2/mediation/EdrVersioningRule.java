package org.meveo.apiv2.mediation;

import javax.annotation.Nullable;
import javax.validation.constraints.NotEmpty;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableEdrVersioningRule.class)
public interface EdrVersioningRule extends Resource {

    @Schema(description = "Defines versioning rules evaluation order")
    @NotEmpty
	Integer getPriority();

    @Schema(description = "Defines if this rule is valid to for this EDR.")
    @NotEmpty
	String getCriteriaEL();

    @Schema(description = "This expression will return string that will be stored as the EDR eventKey")
    @NotEmpty
	String getKeyEL();

    @Schema(description = "This expression will tell us if EDR is a new version of the EDR.")
    @NotEmpty
	String getIsNewVersionEL();

    @Schema(description = "attached mediation settings")
    @Nullable
	Resource getMediationSetting();
}
