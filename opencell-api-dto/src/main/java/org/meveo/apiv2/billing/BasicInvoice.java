package org.meveo.apiv2.billing;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableBasicInvoice.class)
public interface BasicInvoice extends Resource {

	String getBillingAccountCode();

	@Nullable
	String getOrderCode();

	@Nullable
	Date getInvoiceDate();

	@Nullable
	Date getDueDate();

	@Nullable
	String getArticleCode();

	@Nullable
	String getLabel();

	BigDecimal getAmountWithTax();

}
