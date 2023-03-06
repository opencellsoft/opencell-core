package org.meveo.apiv2.dunning;

import java.math.BigDecimal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.dunning.DunningModeEnum;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDunningSettings.class)
public interface DunningSettings extends Resource {

    @Schema(description = "dunning mode")
    @Nonnull
	DunningModeEnum getDunningMode();

    @Schema(description = "Max dunning level", defaultValue = "15")
    @Nullable
	Integer getMaxDunningLevels();

    @Schema(description = "Max days outstanding")
    @Nullable
	Integer getMaxDaysOutstanding();

    @Schema(description = "allow interest for delay", defaultValue = "true")
    @Nullable
    @JsonProperty("allowInterestForDelay")
	Boolean isAllowInterestForDelay();

    @Schema(description = "interest for delay rate")
    @Nullable
	BigDecimal getInterestForDelayRate();

    @Schema(description = "allow dunninf charge", defaultValue = "true")
    @Nullable
    @JsonProperty("allowDunningCharges")
	Boolean isAllowDunningCharges();

    @Schema(description = "apply dunning charge exchange rate", defaultValue = "true")
    @Nullable
    @JsonProperty("applyDunningChargeFxExchangeRate")
	Boolean isApplyDunningChargeFxExchangeRate();

    @Schema(description = "accounting artile attached to this dunning", defaultValue = "true")
    @Nullable
	Resource getAccountingArticle();
    
    @Schema(description = "customer balance attached to this dunning", defaultValue = "true")
    @Nullable
	Resource getCustomerBalance();
}
