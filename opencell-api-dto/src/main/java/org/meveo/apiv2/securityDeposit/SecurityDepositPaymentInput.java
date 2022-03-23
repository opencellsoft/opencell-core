package org.meveo.apiv2.securityDeposit;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableSecurityDepositPaymentInput.class)
public interface SecurityDepositPaymentInput extends Resource {

    @NotNull
    BigDecimal getAmount();

    @NotNull
    Resource getAccountOperation();

}