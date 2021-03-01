package org.meveo.apiv2.billing;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.api.dto.DiscountInvoiceAggregateDto;
import org.meveo.api.dto.RatedTransactionDto;
import org.meveo.api.dto.SubCategoryInvoiceAgregateDto;
import org.meveo.api.dto.SubcategoryInvoiceAgregateAmountDto;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.billing.InvoicePaymentStatusEnum;
import org.meveo.model.billing.InvoiceStatusEnum;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableSubCategoryInvoiceAgregate.class)
public interface SubCategoryInvoiceAgregate extends Resource {

	/**
	 * @return the itemNumber
	 */
	@Nullable
	public Integer getItemNumber();

	/**
	 * @return the accountingCode
	 */
	@Nullable
	public String getAccountingCode();

	/**
	 * @return the description
	 */
	@Nullable
	public String getDescription();

	/**
	 * @return the quantity
	 */
	@Nullable
	public BigDecimal getQuantity();

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
	 * @return the invoiceSubCategoryCode
	 */
	@Nullable
	public String getInvoiceSubCategoryCode();

	/**
	 * @return the userAccountCode
	 */
	@Nullable
	public String getUserAccountCode();

	/**
	 * @return the amountsByTax
	 */
	@Nullable
	public List<SubcategoryInvoiceAgregateAmount> getAmountsByTax();

	/**
	 * @return the ratedTransactions
	 */
	public List<InvoiceLine> getInvoiceLines();

}
