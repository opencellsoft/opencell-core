package org.meveo.apiv2.billing;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableInvoiceExchangeRateInput.class)
public interface InvoiceExchangeRateInput extends Resource {

	@Schema(description = "exchange rate")
    @NotNull
    BigDecimal getExchangeRate();

}