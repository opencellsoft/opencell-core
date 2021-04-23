package org.meveo.apiv2.billing;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableTax.class)
public interface Tax extends Resource {

	@Nullable
	public BigDecimal getPercent();

	@Nullable
	public String getAccountingCode();

}