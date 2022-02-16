package org.meveo.apiv2.securityDeposit;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.payments.PaymentMethodEnum;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableSecurityDepositCreditInput.class)
public interface SecurityDepositCreditInput extends Resource {

    @NotNull
    BigDecimal getAmountToCredit(); 

    @NotNull
    String getBankLot();
    
    @NotNull
    String getCustomerAccountCode();

    @NotNull
    Boolean getIsToMatching();

    @NotNull
    String getOccTemplateCode();

    @NotNull
    String getPaymentInfo();
    
    @NotNull
    String getPaymentInfo1();

    @NotNull
    String getPaymentInfo2();

    @NotNull
    String getPaymentInfo3();

    @NotNull
    String getPaymentInfo4();

    @NotNull
    String getPaymentInfo5();   

    @NotNull
    PaymentMethodEnum getPaymentMethod();

    @NotNull
    String getReference();

}