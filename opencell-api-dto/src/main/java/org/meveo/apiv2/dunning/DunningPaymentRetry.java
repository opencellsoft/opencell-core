package org.meveo.apiv2.dunning;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.api.dto.cpq.xml.PaymentMethod;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.dunning.PayRetryFrequencyUnitEnum;
import org.meveo.model.payments.PaymentMethodEnum;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDunningPaymentRetry.class)
public interface DunningPaymentRetry extends Resource {

    @Schema(description = "The payment method")
    @Nonnull
    PaymentMethodEnum getPaymentMethod();

    @Schema(description = "The payment service provider")
    @Nullable
    String getPsp();

    @Schema(description = "The number of payment retries")
    @Nonnull
    Integer getNumPayRetries();

    @Schema(description = "The unit's frequency of retry")
    @Nonnull
    PayRetryFrequencyUnitEnum getPayRetryFrequencyUnit();

    @Schema(description = "The retry's frequency")
    @Nonnull
    Integer getPayRetryFrequency();

    @Schema(description = "The dunning settings associated to stop reason")
    @Nonnull
	Resource getDunningSettings();
}
