package org.meveo.apiv2.billing;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.generic.GenericPagingAndFiltering;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableInvoiceLinesToMarkAdjustment.class)
public interface InvoiceLinesToMarkAdjustment {
	
    @Nullable
    Boolean getIgnoreInvalidStatuses();

    @Nullable
    @Value.Default default Map<String, Object> getFilters(){ return Collections.emptyMap();}
		

}
