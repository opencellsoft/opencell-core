package org.meveo.apiv2.billing;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableInvoiceLinesToMarkAdjustment.class)
public interface InvoiceLinesToMarkAdjustment {
	
    @Nullable
    Boolean getIgnoreInvalidStatuses();

	@Schema(description = "Containing list of invoice line Ids to mark for adjustment.")
	List<Long> getInvoiceLinesIds();
		

}
