package org.meveo.apiv2.billing;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableCategoryInvoiceAgregate.class)
public interface CategoryInvoiceAgregate extends Resource {

	@Schema(description = "")
	public String getCategoryInvoiceCode();

	/**
	 * @return the description
	 */
	@Schema(description = "")
	@Nullable
	public String getDescription();

	/**
	 * @return the userAccountCode
	 */
	@Schema(description = "")
	@Nullable
	public String getUserAccountCode();

	/**
	 * @return the itemNumber
	 */
	@Schema(description = "")
	@Nullable
	public Integer getItemNumber();

	/**
	 * @return the amountWithoutTax
	 */
	@Schema(description = "")
	@Nullable
	public BigDecimal getAmountWithoutTax();

	/**
	 * @return the amountTax
	 */
	@Schema(description = "")
	@Nullable
	public BigDecimal getAmountTax();

	/**
	 * @return the amountWithTax
	 */
	@Schema(description = "")
	@Nullable
	public BigDecimal getAmountWithTax();

	/**
	 * @return the listSubCategoryInvoiceAgregateDto
	 */
	@Schema(description = "")
	@Nullable
	public List<SubCategoryInvoiceAgregate> getListSubCategoryInvoiceAgregate();

	/**
	 * @return the discountAggregates
	 */
	@Schema(description = "")
	@Nullable
	public List<DiscountInvoiceAggregate> getDiscountAggregates();

}
