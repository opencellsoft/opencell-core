package org.meveo.apiv2.securityDeposit;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.api.dto.CurrencyDto;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.securityDeposit.SecurityDepositStatusEnum;
import org.meveo.model.securityDeposit.SecurityTemplateStatusEnum;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableSecurityDepositTemplate.class)
public interface SecurityDepositTemplate extends Resource {

    @Schema(description = "")
    @NotNull String getTemplateName();

    @Schema(description = "")
    @NotNull Resource getCurrency();

    @Schema(description = "")
     boolean getAllowValidityDate();

    @Schema(description = "")
     boolean getAllowValidityPeriod();

    @Schema(description = "")
    @NotNull BigDecimal getMinAmount();

    @Schema(description = "")
    @NotNull BigDecimal getMaxAmount();

    @Schema(description = "")
    @NotNull SecurityTemplateStatusEnum getStatus();


    @Schema(description = "")
     Integer getNumberOfInstantiation();


}
