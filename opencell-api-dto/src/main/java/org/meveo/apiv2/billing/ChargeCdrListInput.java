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
    @Schema(description = "Rating must happen in a transaction no change performed during rating is persisted if isVirtual=true", name ="isVirtual")
    default boolean isVirtual() {
        return false;
    }

    @Default
    @Schema(description = "Rate all TriggeredEDR created by the rating of the charge", name ="isRateTriggeredEdr")
    default boolean isRateTriggeredEdr() {
        return false;
    }

    @Schema(description = "The max deep used in triggered EDR")
    @Nullable
    Integer getMaxDepth();

    @Default
    @Schema(description = "If true, the API will return the list of IDs of all wallet operations produced. Applies to non-virtual mode only.", name ="isReturnWalletOperations")
    default boolean isReturnWalletOperations() {
        return false;
    }

    @Default
    @Schema(description = "If true, the API will return the list of details of all wallet operations produced, even if they are virtual", name ="isReturnWalletOperationDetails")
    default boolean isReturnWalletOperationDetails() {
        return false;
    }


    @Default
    @Schema(description = "If true, the API will return the list of counter updates produced, even if they are virtual", name = "isReturnCounters")
    default boolean isReturnCounters() {
        return false;
    }

    @Default
    @Schema(description = "If true, the API will automatically generate RTs", name = "isGenerateRTs")
    default boolean isGenerateRTs() {
        return false;
    }
}