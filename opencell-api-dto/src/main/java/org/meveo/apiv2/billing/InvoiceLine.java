/**
 * 
 */
package org.meveo.apiv2.billing;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import org.immutables.value.Value;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.billing.InvoiceLineStatusEnum;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableInvoiceLine.class)
public interface InvoiceLine extends Resource {

	/**
	 * @return the invoice id
	 */
	@Schema(description = "The id of the invoice")
	@Nullable
	Long getInvoiceId();

	/**
	 * @return the prestation
	 */
	@Schema(description = "The prestation")
	@Nullable
	String getPrestation();

	/**
	 * @return the accountingArticle code
	 */
	@Schema(description = "The code of accounting article")
	@NotNull
	String getAccountingArticleCode();

	/**
	 * @return the offerServiceTemplate id
	 */
	@Schema(description = "The id of offer service template")
	@Nullable
	Long getOfferServiceTemplateId();

	/**
	 * @return the product
	 */
	@Schema(description = "The code of product")
	@Nullable
	String getProductCode();

	/**
	 * @return the serviceTemplate code
	 */
	@Schema(description = "The code of service template")
	@Nullable
	String getServiceTemplateCode();

	/**
	 * @return the quantity
	 */
	@Schema(description = "The quantity")
	@Nullable
	BigDecimal getQuantity();

	/**
	 * @return the unitPrice
	 */
	@Schema(description = "The unit price")
	@Nullable
	BigDecimal getUnitPrice();

	/**
	 * @return the discountRate
	 */
	@Schema(description = "The discount rate")
	@Nullable
	BigDecimal getDiscountRate();

	/**
	 * @return the amountWithoutTax
	 */
	@Schema(description = "The amount without tax")
	@Nullable
	BigDecimal getAmountWithoutTax();

	/**
	 * @return the taxRate
	 */
	@Schema(description = "The tax rate")
	@Nullable
	BigDecimal getTaxRate();

	/**
	 * @return the amountWithTax
	 */
	@Schema(description = "The amount with tax")
	@Nullable
	BigDecimal getAmountWithTax();

	/**
	 * @return the amountTax
	 */
	@Schema(description = "The amount tax")
	@Nullable
	BigDecimal getAmountTax();

	/**
	 * @return the discountPlan code
	 */
	@Schema(description = "The code of discount plan")
	@Nullable
	String getDiscountPlanCode();

	/**
	 * @return the tax code
	 */
	@Schema(description = "The code of tax")
	@Nullable
	String getTaxCode();

	/**
	 * @return the orderRef
	 */
	@Schema(description = "The order reference")
	@Nullable
	String getOrderRef();

	/**
	 * @return the accessPoint
	 */
	@Schema(description = "The access point")
	@Nullable
	String getAccessPoint();

	/**
	 * @return the commercialOrder id
	 */
	@Schema(description = "The id of the commercial order")
	@Nullable
	Long getCommercialOrderId();

	/**
	 * @return the billingRun id
	 */
	@Schema(description = "The id of billing run")
	@Nullable
	Long getBillingRunId();

	/**
	 * @return the billingAccount code
	 */
	@Schema(description = "The code of billing account")
	@Nullable
	String getBillingAccountCode();

	/**
	 * @return the valueDate
	 */
	@Schema(description = "The date value")
	@Nullable
	Date getValueDate();

	/**
	 * @return the orderNumber
	 */
	@Schema(description = "The order number")
	@Nullable
	String getOrderNumber();

	/**
	 * @return the discountAmount
	 */
	@Schema(description = "The discount amount")
	@Nullable
	BigDecimal getDiscountAmount();

	/**
	 * @return the label
	 */
	@Schema(description = "The label for invoice line")
	@Nullable
	String getLabel();

	/**
	 * @return the rawAmount
	 */
	@Schema(description = "The raw amount")
	@Nullable
	BigDecimal getRawAmount();

	/**
	 * @return the serviceInstance
	 */
	@Schema(description = "The code of service instance")
	@Nullable
	String getServiceInstanceCode();

	/**
	 * @return the subscription code
	 */
	@Schema(description = "The code of subscription")
	@Nullable
	String getSubscriptionCode();

	/**
	 * @return the offerTemplate code
	 */
	@Schema(description = "The code of offer template")
	@Nullable
	String getOfferTemplateCode();

	/**
	 * @return the productVersion id
	 */
	@Schema(description = "The id of product version")
	@Nullable
	Long getProductVersionId();

	/**
	 * @return the orderLot code
	 */
	@Schema(description = "The orderLot code")
	@Nullable
	String getOrderLotCode();

	/**
	 * @return the taxRecalculated
	 */
	@Schema(description = "the tax recalculated")
	@Nullable
	Boolean isTaxRecalculated();

	/**
	 * @return the status
	 */
	@Schema(description = "the status", example = "possible value are: OPEN, BILLED, REJECTED, RERATED, CANCELED")
	@Nullable
	InvoiceLineStatusEnum getStatus();
	/**
	 * 
	 * @return The description
	 */
	@Schema(description = "The description")
	@Nullable
	String getDescription();
	/**
	 * 
	 * @return The start date
	 */
	@Schema(description = "The start date")
	@Nullable
	Date getStartDate();
	/**
	 * 
	 * @return The end date
	 */
	@Schema(description = "The end date")
	@Nullable
	Date getEndDate();
	
	@Schema(description = "The Tax Mode")
    @Nullable
    String getTaxMode();
	
	@Schema(description = "The Tax Accounting Code")
    @Nullable
    String getTaxAccountingCode();

	@Nullable
	@Schema(description = "custom field associated to accounting article")
	CustomFieldsDto getCustomFields();
	
	@Schema(description = "The unit price currency")
	@Nullable
	String getUnitPriceCurrency();

	@Schema(description = "The user account code")
	@Nullable
	String getUserAccountCode();

}
