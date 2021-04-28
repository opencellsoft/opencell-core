package org.meveo.apiv2.billing;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableInvoiceLineInput.class)
public interface InvoiceLineInput{

	InvoiceLine getInvoiceLine();

	@Schema(description = "Indicate if the invoice line will skip validation")
	@Nullable
	Boolean getSkipValidation();
}
