package org.meveo.apiv2.securityDeposit;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableSecurityDepositSettings.class)
 public interface SecurityDepositSettings  extends Resource {


    @Schema(description = "")
    @NotNull Boolean getUseSecurityDeposit();
    @Schema(description = "")
    @NotNull
     BigDecimal getMaxAmountPerSecurityDeposit();
    @Schema(description = "")
    @NotNull
     BigDecimal getMaxAmountPerCustomer();
    @Schema(description = "")
    @NotNull
     Boolean getAutoRefund();
    @Schema(description = "")
    @NotNull
     Boolean getAllowRenew();
    @Schema(description = "")
    @NotNull
     Boolean getAllowTransfer();



}
