package org.meveo.apiv2.billing;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableCategoryInvoiceAgregate.class)
public interface CategoryInvoiceAgregate extends Resource {

	public String getCategoryInvoiceCode();

	/**
	 * @return the description
	 */
	@Nullable
	public String getDescription();

	/**
	 * @return the userAccountCode
	 */
	@Nullable
	public String getUserAccountCode();

	/**
	 * @return the itemNumber
	 */
	@Nullable
	public Integer getItemNumber();

	/**
	 * @return the amountWithoutTax
	 */
	@Nullable
	public BigDecimal getAmountWithoutTax();

	/**
	 * @return the amountTax
	 */
	@Nullable
	public BigDecimal getAmountTax();

	/**
	 * @return the amountWithTax
	 */
	@Nullable
	public BigDecimal getAmountWithTax();

	/**
	 * @return the listSubCategoryInvoiceAgregateDto
	 */
	@Nullable
	public List<SubCategoryInvoiceAgregate> getListSubCategoryInvoiceAgregate();

	/**
	 * @return the discountAggregates
	 */
	@Nullable
	public List<DiscountInvoiceAggregate> getDiscountAggregates();

}
