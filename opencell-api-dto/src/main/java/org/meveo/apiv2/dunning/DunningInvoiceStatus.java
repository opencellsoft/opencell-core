package org.meveo.apiv2.dunning;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.dunning.DunningInvoiceStatusContextEnum;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDunningInvoiceStatus.class)
public interface DunningInvoiceStatus extends Resource {

    @Schema(description = "The invoice status")
    @Nonnull
    String getStatus();

    @Schema(description = "The invoice status's context")
    @Nonnull
    DunningInvoiceStatusContextEnum getContext();

    @Schema(description = "The dunning settings associated to invoice status")
    @Nonnull
	Resource getDunningSettings();
}
