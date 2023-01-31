package org.meveo.apiv2.billing;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDiscountPlanInstanciateDto.class)
public interface DiscountPlanInstanciateDto {

    @Schema(description = "discount plan to instanciate")
    @Nullable
    Resource getDiscountPlan();

    @Schema(description = "if subscription is set and serveiceInstance is null then the discount will be instanciate to subscription")
    @Nullable
    Resource getSubscription();
    
    @Schema(description = "if the serviceInstance is set then the discount plan will be instanciate for service instance")
    @Nullable
    Resource getServiceInstance();

    @Schema(description = "if billingAccount is set and serveiceInstance and subscription  are null then the discount will be instanciate to subscription")
    @Nullable
    Resource getBillingAccount();
}