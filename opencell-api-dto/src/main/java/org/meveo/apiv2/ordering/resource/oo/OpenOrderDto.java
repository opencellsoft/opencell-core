package org.meveo.apiv2.ordering.resource.oo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.apiv2.ordering.resource.order.ThresholdInput;
import org.meveo.model.cpq.tags.Tag;
import org.meveo.model.ordering.OpenOrderStatusEnum;
import org.meveo.model.ordering.OpenOrderTypeEnum;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
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
