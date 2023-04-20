package org.meveo.apiv2.accounts;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.billing.CounterPeriodDto;

import javax.annotation.Nullable;
import java.util.Set;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableCounterInstanceDto.class)
public interface CounterInstanceDto {

    @Nullable
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
    String getProductCode();

    @Nullable
    String getChargeInstanceCode();

    @Schema(description = "Counter Periods")
    @Nullable
    Set<CounterPeriodDto> getCounterPeriods();

}