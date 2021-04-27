package org.meveo.apiv2.billing;

import java.math.BigDecimal;

import org.immutables.value.Value;
import org.meveo.api.dto.TaxDto;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableSubcategoryInvoiceAgregateAmount.class)
public interface SubcategoryInvoiceAgregateAmount extends Resource {

	@Schema(description = "The amount without tax")
	public BigDecimal getAmountWithoutTax();

	/**
	 * @return the amountWithTax
	 */
	@Schema(description = "The amount with tax")
	public BigDecimal getAmountWithTax();

	/**
	 * @return the amountTax
	 */
	@Schema(description = "The amount tax")
	public BigDecimal getAmountTax();

	/**
	 * @return the tax
	 */
	@Schema(description = "The tax attachaed to sub category invoice agregate amount")
	public Tax getTax();

}
