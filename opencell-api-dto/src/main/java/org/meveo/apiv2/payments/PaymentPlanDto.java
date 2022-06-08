package org.meveo.apiv2.payments;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.model.payments.ActionOnRemainingAmountEnum;
import org.meveo.model.payments.RecurrenceUnitEnum;
import org.meveo.model.payments.plan.PaymentPlanStatusEnum;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutablePaymentPlanDto.class)
public interface PaymentPlanDto {

    @NotNull
    BigDecimal getAmountToRecover();

    @NotNull
    BigDecimal getAmountPerInstallment();

    @NotNull
    BigDecimal getRemainingAmount();

    @NotNull
    ActionOnRemainingAmountEnum getActionOnRemainingAmount();

    @NotNull
    Integer getNumberOfInstallments();

    @NotNull
    Date getStartDate();

    Date getEndDate();

    @NotNull
    RecurrenceUnitEnum getRecurringUnit();

    @NotNull
    PaymentPlanStatusEnum getStatus();

    @Schema(description = "List of Installment AccountOperation")
    @NotEmpty
    Set<InstallmentAccountOperation> getInstallmentAccountOperations();

    @NotNull
    Long getCustomerAccount();
}