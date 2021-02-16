/**
 * 
 */
package org.meveo.apiv2.billing;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.billing.InvoiceLineStatusEnum;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableInvoiceLine.class)
public interface InvoiceLine extends Resource {
	
	/**
	 * @return the invoice id
	 */
	@Nullable
	Long getInvoiceId();

	/**
	 * @return the prestation
	 */
	@Nullable
	String getPrestation();

	/**
	 * @return the accountingArticle code
	 */
	@Nullable
	String getAccountingArticleCode();

	/**
	 * @return the offerServiceTemplate code
	 */
	@Nullable
	String getOfferServiceTemplateCode();

	/**
	 * @return the product
	 */
	@Nullable
	String getProductCode();

	/**
	 * @return the serviceTemplate code
	 */
	@Nullable
	String getServiceTemplateCode();

	/**
	 * @return the quantity
	 */
	@Nullable
	BigDecimal getQuantity();

	/**
	 * @return the unitPrice
	 */
	@Nullable
	BigDecimal getUnitPrice();

	/**
	 * @return the discountRate
	 */
	@Nullable
	BigDecimal getDiscountRate();

	/**
	 * @return the amountWithoutTax
	 */
	@Nullable
	BigDecimal getAmountWithoutTax();

	/**
	 * @return the taxRate
	 */
	@Nullable
	BigDecimal getTaxRate();

	/**
	 * @return the amountWithTax
	 */
	@Nullable
	BigDecimal getAmountWithTax();

	/**
	 * @return the amountTax
	 */
	@Nullable
	BigDecimal getAmountTax();

	/**
	 * @return the discountPlan code
	 */
	@Nullable
	String getDiscountPlanCode();

	/**
	 * @return the tax code
	 */
	@Nullable
	String getTaxCode();

	/**
	 * @return the orderRef
	 */
	@Nullable
	String getOrderRef();

	/**
	 * @return the accessPoint
	 */
	@Nullable
	String getAccessPoint();

	/**
	 * @return the commercialOrder code
	 */
	@Nullable
	String getCommercialOrderCode();

	/**
	 * @return the billingRun code
	 */
	@Nullable
	String getBillingRunCode();

	/**
	 * @return the billingAccount code
	 */
	@Nullable
	String getBillingAccountCode();

	/**
	 * @return the valueDate
	 */
	@Nullable
	Date getValueDate();

	/**
	 * @return the orderNumber
	 */
	@Nullable
	String getOrderNumber();

	/**
	 * @return the discountAmount
	 */
	@Nullable
	BigDecimal getDiscountAmount();

	/**
	 * @return the label
	 */
	@Nullable
	String getLabel();

	/**
	 * @return the rawAmount
	 */
	@Nullable
	BigDecimal getRawAmount();

	/**
	 * @return the serviceInstance
	 */
	@Nullable
	String getServiceInstanceCode();

	/**
	 * @return the subscription code
	 */
	@Nullable
	String getSubscriptionCode();

	/**
	 * @return the offerTemplate code
	 */
	@Nullable
	String getOfferTemplateCode();

	/**
	 * @return the productVersion code
	 */
	@Nullable
	String getProductVersionCode();

	/**
	 * @return the orderLot code
	 */
	@Nullable
	String getOrderLotCode();

	/**
	 * @return the taxRecalculated
	 */
	@Nullable
	Boolean isTaxRecalculated();

	/**
	 * @return the status
	 */
	@Nullable
	InvoiceLineStatusEnum getStatus();
}
