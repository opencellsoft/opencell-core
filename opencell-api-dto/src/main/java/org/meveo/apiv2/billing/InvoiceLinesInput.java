package org.meveo.apiv2.billing;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableInvoiceLinesInput.class)
public interface InvoiceLinesInput {

	List<InvoiceLine> getInvoiceLines();

	@Schema(description = "Indicate if the invoice line will skip validation")
	@Nullable
	Boolean getSkipValidation();
}
