package org.meveo.apiv2.securityDeposit;

import static java.lang.Boolean.FALSE;

import java.math.BigDecimal;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.meveo.apiv2.settings.OpenOrderSettingInput;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableFinanceSettings.class)
public interface FinanceSettings extends Resource {

    @NotNull
    Boolean getUseSecurityDeposit();

    @Nullable
    BigDecimal getMaxAmountPerSecurityDeposit();

    @Nullable
    BigDecimal getMaxAmountPerCustomer();

    @NotNull
    Boolean getAutoRefund();

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
}