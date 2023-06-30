package org.meveo.apiv2.securityDeposit;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.securityDeposit.SecurityDepositStatusEnum;
import org.meveo.model.securityDeposit.ValidityPeriodUnit;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableSecurityDepositInput.class)
public interface SecurityDepositInput extends Resource {

    @Nullable
    String getDescription();

    @NotNull
    Resource getTemplate();
 
    @Nullable
    Resource getCurrency();

    @Nullable
    Resource getCustomerAccount();

    @Nullable
    Date getValidityDate();

    @Nullable
    Integer getValidityPeriod();

    @Nullable
    ValidityPeriodUnit getValidityPeriodUnit();

    @Nullable
    BigDecimal getAmount();

    @Nullable
    BigDecimal getCurrentBalance();

    @Nullable
    SecurityDepositStatusEnum getStatus();

    @Nullable
    Resource getSubscription();

    @Nullable
    String getExternalReference();
    
    @Nullable
    Resource getServiceInstance();
    
    @Nullable
    String getRefundReason();
    
    @Nullable
    String getCancelReason();

    @Nullable
    Resource getLinkedInvoice();
    
    @NotNull
    Resource getBillingAccount();

    @NotNull
    Resource getSeller();
}