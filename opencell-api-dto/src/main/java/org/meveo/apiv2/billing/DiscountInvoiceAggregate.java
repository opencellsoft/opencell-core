package org.meveo.apiv2.billing;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDiscountInvoiceAggregate.class)
public interface DiscountInvoiceAggregate extends Resource {

	@Nullable
	public String getDiscountPlanItemCode();

	@Nullable
	public BigDecimal getDiscountPercent();

}