package org.meveo.apiv2.securityDeposit;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableFinanceSettings.class)
public interface FinanceSettings extends Resource {

    @NotNull
    Boolean getUseSecurityDeposit();

    @NotNull
    BigDecimal getMaxAmountPerSecurityDeposit();

    @NotNull
    BigDecimal getMaxAmountPerCustomer();

    @NotNull
    Boolean getAutoRefund();

    @NotNull
    Boolean getAllowRenew();

    @NotNull
    Boolean getAllowTransfer();
}
