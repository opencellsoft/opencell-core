package org.meveo.apiv2.billing;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDiscountInvoiceAggregate.class)
public interface DiscountInvoiceAggregate extends Resource {

	@Schema(description = "The code of discount plan item")
	@Nullable
	public String getDiscountPlanItemCode();

	@Schema(description = "The discount percent")
	@Nullable
	public BigDecimal getDiscountPercent();

}