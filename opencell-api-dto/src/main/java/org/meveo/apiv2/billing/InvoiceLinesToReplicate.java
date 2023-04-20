package org.meveo.apiv2.billing;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableInvoiceLinesToReplicate.class)
public interface InvoiceLinesToReplicate {

    @Nullable
    Boolean getGlobalAdjustment();

	@Schema(description = "Containing list of invoice line Ids to replicate in the new adjustment.")
	List<Long> getInvoiceLinesIds();

    @Schema(description = "Containing list of invoice line Ids to replicate in the new adjustment.")
    List<InvoiceLineRTs> getInvoiceLinesRTs();
}
