package org.meveo.apiv2.billing;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableRatedTransactionInput.class)
public interface RatedTransactionInput extends Resource {

	@Nullable
	String getBillingAccountCode();

	@Nullable
	String getUserAccountCode();

	@Nullable
	String getSubscriptionCode();

	@Nullable
	String getServiceInstanceCode();

	@Nullable
	String getChargeInstanceCode();

	BigDecimal getUnitAmountWithoutTax();

	BigDecimal getQuantity();
}