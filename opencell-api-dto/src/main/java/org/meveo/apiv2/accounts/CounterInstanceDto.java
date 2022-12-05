package org.meveo.apiv2.accounts;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.billing.CounterPeriodDto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableCounterInstanceDto.class)
public interface CounterInstanceDto {

    @NotNull
    String getCode();

    @Nullable
    String getDescription();

    @NotNull
    String getCounterTemplateCode();

    @Nullable
    String getCustomerAccountCode();

    @Nullable
    String getBillingAccountCode();

    @Nullable
    String getUserAccountCode();

    @Nullable
    String getSubscriptionCode();

    @Nullable
    String getServiceInstanceCode();

    @Nullable
    Set<String> getChargeInstances();

    @Schema(description = "Counter Periods")
    @Nullable
    Set<CounterPeriodDto> getCounterPeriods();

}