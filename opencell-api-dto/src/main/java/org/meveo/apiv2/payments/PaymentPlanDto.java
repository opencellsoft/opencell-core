package org.meveo.apiv2.payments;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.ActionOnRemainingAmountEnum;
import org.meveo.model.payments.RecurrenceUnitEnum;
import org.meveo.model.payments.plan.PaymentPlanStatusEnum;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutablePaymentPlanDto.class)
interface PaymentPlanDto {

    @NotNull
    BigDecimal getAmountToRecover();

    @NotNull
    BigDecimal getAmountPerInstallment();

    @NotNull
    BigDecimal getRemainingAmount();

    @NotNull
    ActionOnRemainingAmountEnum getActionOnRemainingAmount();

    @NotNull
    BigDecimal getNumberOfInstallments();

    @NotNull
    LocalDate getStartDate();

    @NotNull
    LocalDate getEndDate();

    @NotNull
    RecurrenceUnitEnum getRecurringUnit();

    @NotNull
    PaymentPlanStatusEnum getStatus();

    @Schema(description = "List of AccountOperation and Sequence for matching")
    @NotEmpty
    Set<AccountOperation> getAccountOperations();

    @NotNull
    Long getCustomerAccount();
}