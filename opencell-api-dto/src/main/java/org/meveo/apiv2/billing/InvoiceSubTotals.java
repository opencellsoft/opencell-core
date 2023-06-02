package org.meveo.apiv2.billing;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableInvoiceSubTotals.class)
public interface InvoiceSubTotals extends Resource{

	@Schema(description = "invoice type")
	@Nullable
	Resource getInvoiceType();

	@Schema(description = "sub total el to be evaluated")
	@Nullable
	String getSubTotalEl();

	@Schema(description = "label of the invoice sub total")
	@Nullable
	String getLabel();

	@Schema(description = "sum of the amount with tax")
	@Nullable
	BigDecimal getAmountTax();

	@Schema(description = "sum of the amount without tax")
	@Nullable
	BigDecimal getAmountWithoutTax();


	@Schema(description = "sum of transactional the amount with tax")
	@Nullable
	BigDecimal getTransactionalAmountTax();

	@Schema(description = "sum of transactional the amount without tax")
	@Nullable
	BigDecimal getTransactionalAmountWithoutTax();
	
}
