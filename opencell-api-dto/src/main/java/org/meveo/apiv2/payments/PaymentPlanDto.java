package org.meveo.apiv2.payments;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.model.payments.ActionOnRemainingAmountEnum;
import org.meveo.model.payments.RecurrenceUnitEnum;
import org.meveo.model.payments.plan.PaymentPlanStatusEnum;

import javax.annotation.Nullable;
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
    String getCode();

    @Nullable
    String getDescription();

    @NotNull
    BigDecimal getAmountToRecover();

    @NotNull
    BigDecimal getAmountPerInstallment();

    @NotNull
    ActionOnRemainingAmountEnum getActionOnRemainingAmount();

    @NotNull
    Integer getNumberOfInstallments();

    @NotNull
    Date getStartDate();

    @NotNull
    RecurrenceUnitEnum getRecurringUnit();

    @Schema(description = "List of Installment AccountOperation")
    @NotEmpty
    Set<InstallmentAccountOperation> getTargetedAos();

    @NotNull
    Long getCustomerAccount();

    @Nullable
    PaymentPlanStatusEnum getStatus();

    @Nullable
    Date getEndDate();
}