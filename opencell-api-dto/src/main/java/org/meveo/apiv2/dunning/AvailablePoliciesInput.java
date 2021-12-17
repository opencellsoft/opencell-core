package org.meveo.apiv2.dunning;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableAvailablePoliciesInput.class)
public interface AvailablePoliciesInput {

    @Schema(description = "Billing account resource")
    @Nullable
    Resource getBillingAccount();

    @Schema(description = "Collection plan resource")
    @Nullable
    Resource getCollectionPlan();
}