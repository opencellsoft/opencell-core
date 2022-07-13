package org.meveo.apiv2.mediation;

import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.DefaultValue;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableMediationSetting.class)
public interface MediationSetting extends Resource {

    @Schema(description = "enable edr versioning")
    @DefaultValue("true")
	boolean getEnableEdrVersioning();

    @Schema(description = "list of rules for edr versioning")
    @Nullable
	Set<EdrVersioningRule> getRules();
}
