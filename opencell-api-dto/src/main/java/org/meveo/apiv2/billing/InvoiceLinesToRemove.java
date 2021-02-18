package org.meveo.apiv2.billing;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableInvoiceLinesToRemove.class)
public interface InvoiceLinesToRemove {
	
	List<Long> getIds();
	
	@Nullable
	Boolean getSkipValidation();
}