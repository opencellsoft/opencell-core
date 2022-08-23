package org.meveo.apiv2.ordering.resource.oo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.apiv2.ordering.resource.order.ThresholdInput;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableOpenOrderDto.class)
public interface OpenOrderDto extends Resource  {

	@Nullable
	String getExternalReference();

	@Nullable
	String getDescription();

	@Nullable
	Date getEndOfValidityDate();

	@Nullable
	List<ThresholdInput> getThresholds();

	@Nullable
	List<String> getTags();

	@Nullable
	String getCancelReason();
}
