package org.meveo.apiv2.billing;

import javax.annotation.Nullable;
import org.immutables.value.Value;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.apiv2.models.Resource;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableInvoicePatchInput.class)
public interface InvoicePatchInput extends Resource {

	@Nullable
    @Schema(description = "The custom fields associated to the invoice")
    CustomFieldsDto getCustomFields();

    @Schema(description = "The comment for the invoice")
    @Nullable
    String getComment();
    
	@Schema(description = "The external purchase order number")
	@Nullable
	String getPurchaseOrder();
}