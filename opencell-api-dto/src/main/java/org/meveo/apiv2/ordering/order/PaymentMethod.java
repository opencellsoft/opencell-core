package org.meveo.apiv2.ordering.order;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.payments.PaymentMethodEnum;

import javax.annotation.Nullable;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutablePaymentMethod.class)
public interface PaymentMethod extends Resource {
    @Nullable
    @Schema(description = "Payment Method types")
    PaymentMethodEnum getType();
}
