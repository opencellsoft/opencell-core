package org.meveo.apiv2.securityDeposit;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.apiv2.settings.OpenOrderSettingInput;
import org.meveo.model.securityDeposit.ArticleSelectionModeEnum;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

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

    @Value.Default
    @Schema(description = "Enable Billing Redirection Rules")
    default Boolean getEnableBillingRedirectionRules() {
        return FALSE;
    }
    
    @Value.Default
    @Schema(description = "Enable Billing Redirection Rules")
    default Boolean getDiscountAdvancedMode() {
        return FALSE;
    }
    
    @Value.Default
    @Schema(description = "Enable Price List")
    default Boolean getEnablePriceList() {
        return FALSE;
    }
	
	@Nullable
	@Schema(description = "determinate if the article will be compute before or after pricing")
	ArticleSelectionModeEnum getArticleSelectionMode();
	
	@Schema(description = "Entities with Huge Volume")
    Map<String, List<String>> getEntitiesWithHugeVolume();

    @Value.Default
    @Schema(description = "Display warning before process billing Run")
    default boolean getBillingRunProcessWarning() {
        return false;
    }
    
    @Value.Default
    @Schema(description = "Number of elements to process in a synchronous mode")
    default Integer getSynchronousMassActionLimit() {
        return 10000;
    }
}