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

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableSubCategoryInvoiceAgregate.class)
public interface SubCategoryInvoiceAgregate extends Resource {

	/**
	 * @return the itemNumber
	 */
	@Schema(description = "The item number")
	@Nullable
	public Integer getItemNumber();

	/**
	 * @return the accountingCode
	 */
	@Schema(description = "The code of accounting")
	@Nullable
	public String getAccountingCode();

	/**
	 * @return the description
	 */
	@Schema(description = "The description")
	@Nullable
	public String getDescription();

	/**
	 * @return the quantity
	 */
	@Schema(description = "The quantity")
	@Nullable
	public BigDecimal getQuantity();

	/**
	 * @return the amountWithoutTax
	 */
	@Schema(description = "The amount without tax")
	@Nullable
	public BigDecimal getAmountWithoutTax();

	/**
	 * @return the amountTax
	 */
	@Schema(description = "The amount tax")
	@Nullable
	public BigDecimal getAmountTax();

	/**
	 * @return the amountWithTax
	 */
	@Schema(description = "The amount with tax")
	@Nullable
	public BigDecimal getAmountWithTax();

	/**
	 * @return the invoiceSubCategoryCode
	 */
	@Schema(description = "The code of invoice sub category")
	@Nullable
	public String getInvoiceSubCategoryCode();

	/**
	 * @return the userAccountCode
	 */
	@Schema(description = "The code of user account")
	@Nullable
	public String getUserAccountCode();

	/**
	 * @return the amountsByTax
	 */
	@Schema(description = "List of amounts by tax")
	@Nullable
	public List<SubcategoryInvoiceAgregateAmount> getAmountsByTax();

	/**
	 * @return the ratedTransactions
	 */
	@Schema(description = "The list of invoice lines")
	public List<InvoiceLine> getInvoiceLines();

}
