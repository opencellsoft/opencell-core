package org.meveo.apiv2.billing;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableInvoiceInput.class)
public interface InvoiceInput {

	@Schema(description = "Information of the invoice")
	Invoice getInvoice();

	@Schema(description = "Indicate if validation is skept")
	@Nullable
	Boolean getSkipValidation();

	@Schema(description = "Indicate if the invoice is DRAFT")
	@Nullable
	Boolean getIsDraft();

	@Schema(description = "Indicate if the invoice is virtual")
	@Nullable
	Boolean getIsVirtual();

	@Schema(description = "Indicate if we want include balance to the invoice")
	@Nullable
	Boolean getIsIncludeBalance();

	@Schema(description = "Indicate if the invoice set auto validation")
	@Nullable
	Boolean getIsAutoValidation();
}
