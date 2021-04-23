package org.meveo.apiv2.billing;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableInvoiceInput.class)
public interface InvoiceInput {

	Invoice getInvoice();

	@Nullable
	Boolean getSkipValidation();

	@Nullable
	Boolean getIsDraft();

	@Nullable
	Boolean getIsVirtual();

	@Nullable
	Boolean getIsIncludeBalance();

	@Nullable
	Boolean getIsAutoValidation();
}
