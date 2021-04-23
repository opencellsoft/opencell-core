package org.meveo.apiv2.billing;

import java.math.BigDecimal;

import org.immutables.value.Value;
import org.meveo.api.dto.TaxDto;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableSubcategoryInvoiceAgregateAmount.class)
public interface SubcategoryInvoiceAgregateAmount extends Resource {

	public BigDecimal getAmountWithoutTax();

	/**
	 * @return the amountWithTax
	 */
	public BigDecimal getAmountWithTax();

	/**
	 * @return the amountTax
	 */
	public BigDecimal getAmountTax();

	/**
	 * @return the tax
	 */
	public Tax getTax();

}
