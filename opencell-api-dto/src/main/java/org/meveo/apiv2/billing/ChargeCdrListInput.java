package org.meveo.apiv2.billing;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.immutables.value.Value.Default;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonDeserialize(as = ImmutableChargeCdrListInput.class)
public interface ChargeCdrListInput extends CdrListInput {

    @Default
    @Schema(description = "Rating must happen in a transaction no change performed during rating is persisted if isVirtual=true")
    default boolean isVirtual() {
        return false;
    }

    @Default
    @Schema(description = "Rate all TriggeredEDR created by the rating of the charge")
    default boolean isRateTriggeredEdr() {
        return false;
    }

    @Default
    @Schema(description = "If true, the API will return the list of all wallet operations produced during, even if the are virtual")
    default boolean isReturnWalletOperations() {
        return false;
    }

    @Schema(description = "The max deep used in triggered EDR")
    @Nullable
    Integer getMaxDepth();
}