package org.meveo.apiv2.payments;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableClearingResponse.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ClearingResponse {

    String getStatus();

    String getMassage();

    @Nullable
    String getAssociatedPaymentGatewayCode();

    Integer getClearedCodesCount();
}
