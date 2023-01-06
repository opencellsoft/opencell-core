package org.meveo.apiv2.standardReport;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.generic.GenericPagingAndFiltering;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;
import java.util.Date;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableAgedReceivablesDto.class)
public interface AgedReceivablesDto extends Resource {

	@Schema(description = "Indicate the customer account code")
	@Nullable
	GenericPagingAndFiltering getSearchConfig();
}

