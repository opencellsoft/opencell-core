package org.meveo.apiv2.securityDeposit;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.meveo.apiv2.settings.OpenOrderSettingInput;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableFinanceSettings.class)
public interface FinanceSettings extends Resource {

    @Value.Default
    @Schema(description = "use security deposit")
    default Boolean getUseSecurityDeposit() {
        return TRUE;
    }

    @Nullable
    BigDecimal getMaxAmountPerSecurityDeposit();

    @Nullable
    BigDecimal getMaxAmountPerCustomer();

    @Value.Default
    @Schema(description = "Auto refund")
    default Boolean getAutoRefund() {
        return FALSE;
    }

    @Value.Default
    @Schema(description = "Use auxiliary accounting")
    default Boolean getUseAuxiliaryAccounting() {
        return FALSE;
    }

    @Schema(description = "Auxiliary account code El")
    @Nullable
    String getAuxiliaryAccountCodeEl();

    @Schema(description = "Auxiliary account label El")
    @Nullable
    String getAuxiliaryAccountLabelEl();

    @Nullable
    OpenOrderSettingInput getOpenOrderSetting();

    @Value.Default
    @Schema(description = "Activate dunning")
    default Boolean getActivateDunning() {
        return FALSE;
    }
	@Schema(description = "Entities with Huge Volume")
    Map<String, List<String>> getEntitiesWithHugeVolume();

    @Nullable
    @Schema(description = "Wallet Operation partition Period in Months")
    Integer getWoPartitionPeriod();

    @Nullable
    @Schema(description = "Rated Transaction partition Period in Months")
    Integer getRtPartitionPeriod();

    @Nullable
    @Schema(description = "EDR partition Period in Months")
    Integer getEdrPartitionPeriod();

}