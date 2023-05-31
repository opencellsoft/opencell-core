package org.meveo.apiv2.admin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;
@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableInvoiceTypeSellerSequence.class)
public interface InvoiceTypeSellerSequence {
	
	@Nullable
	public Long getId();
	@Nullable
	public Long getInvoiceTypeId();
	@Nullable
	public Long getInvoiceSequenceId();
	@Nullable
	public String getPrefixEL();
	
}
