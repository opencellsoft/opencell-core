package org.meveo.apiv2.billing;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.billing.InvoicePaymentStatusEnum;
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.model.payments.PaymentMethodEnum;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableInvoice.class)
public interface Invoice extends Resource {

	@Schema(description = "The billing account code")
	@Nullable
	String getBillingAccountCode();

	@Schema(description = "The billing run")
	@Nullable
	Resource getBillingRun();

	@Schema(description = "The recorded invoice")
	@Nullable
	Resource getRecordedInvoice();

	@Schema(description = "The invoice number")
	@Nullable
	String getInvoiceNumber();

	@Schema(description = "The product date")
	@Nullable
	Date getProductDate();

	@Schema(description = "The invoice date")
	@Nullable
	Date getInvoiceDate();

	@Schema(description = "The status of the invoice.", example = "possible value are : "
			+ "<ul><li> NEW : invoice entity has been created but incomplete </li>"
			+ "<li>SUSPECT : invoice has been marked as suspect by automatic controls (this status doesn’t block automatic generation)</li>"
			+ "<li>REJECTED : invoice has been rejected by automatic controls (this status block automatic generation)</li>"
			+ "<li>DRAFT : invoice is complete but not validated. It can be edited.</li>"
			+ "<li>CANCELED : invoice has been canceled (all related rated transactions are released. This is a final status)</li>"
			+ "<li>VALIDATED : invoice is validated and cannot be edited anymore (this a final status)</li></ul>")
	@Nullable
	InvoiceStatusEnum getStatus();

	@Schema(description = "The Due date")
	@Nullable
	Date getDueDate();

	@Schema(description = "The amount of the invoice")
	@Nullable
	BigDecimal getAmount();

	@Schema(description = "The discount if any")
	@Nullable
	BigDecimal getDiscount();

	@Schema(description = "The amount without tax")
	@Nullable
	BigDecimal getAmountWithoutTax();

	@Schema(description = "The amount tax")
	@Nullable
	BigDecimal getAmountTax();

	@Schema(description = "The amount with tax")
	@Nullable
	BigDecimal getAmountWithTax();

	@Schema(description = "The net to pay")
	@Nullable
	BigDecimal getNetToPay();

	@Schema(description = "The payment method type", example = "possible value are: CHECK, DIRECTDEBIT, WIRETRANSFER, CARD, PAYPAL, STRIPE, CASH")
	@Nullable
	PaymentMethodEnum getPaymentMethodType();

	@Schema(description = "The iban")
	@Nullable
	String getIban();

	@Schema(description = "The alias")
	@Nullable
	String getAlias();

	@Schema(description = "The tradinf currency id")
	@Nullable
	Resource getTradingCurrency();

	@Schema(description = "The trading country id")
	@Nullable
	Resource getTradingCountry();

	@Schema(description = "The trading language id")
	@Nullable
	Resource getTradingLanguage();

	@Schema(description = "The comment for the invoice")
	@Nullable
	String getComment();

	@Schema(description = "Indicate if the invoice is detailed")
	@Nullable
	@JsonProperty("detailedInvoice")
	Boolean isDetailedInvoice();

	@Schema(description = "The adjusted invoice id")
	@Nullable
	Resource getAdjustedInvoice();

	@Schema(description = "The invoice type code")
	@Nullable
	String getInvoiceTypeCode();

	@Schema(description = "The custom field value")
	@Nullable
	Resource getCfValues();

	@Schema(description = "The custom field accimalated value")
	@Nullable
	Resource getCfAccumulatedValues();

	@Schema(description = "The quote attached to the invoice")
	@Nullable
	Resource getQuote();

	@Schema(description = "The subscription attached to this invoice")
	@Nullable
	Resource getSubscription();

	@Schema(description = "The Order attached to this invoice")
	@Nullable
	Resource getOrder();

	@Schema(description = "The xml file name for generated invoice xml")
	@Nullable
	String getXmlFilename();

	@Schema(description = "The pdf file name for generated invoice PDF")
	@Nullable
	String getPdfFilename();

	@Schema(description = "The payment method attached to this invoice")
	@Nullable
	Resource getPaymentMethod();

	@Schema(description = "The due balance")
	@Nullable
	BigDecimal getDueBalance();

	@Schema(description = "Indicate if the invoice is already sent")
	@Nullable
	@JsonProperty("alreadySent")
	Boolean isAlreadySent();

	@Schema(description = "Indicate if the invoice doesnt send")
	@Nullable
	@JsonProperty("dontSend")
	Boolean isDontSend();

	@Schema(description = "The seller code")
	@Nullable
	String getSellerCode();

	@Schema(description = "Indicate if the invoice is a prepaid")
	@Nullable
	@JsonProperty("prepaid")
	Boolean isPrepaid();

	@Schema(description = "The external ref")
	@Nullable
	String getExternalRef();

	@Schema(description = "The rejected reason")
	@Nullable
	String getRejectReason();

	@Schema(description = "The initial collection date")
	@Nullable
	Date getInitialCollectionDate();

	@Schema(description = "The date of the status")
	@Nullable
	Date getStatusDate();

	@Schema(description = "The xml date")
	@Nullable
	Date getXmlDate();

	@Schema(description = "The pdf date")
	@Nullable
	Date getPdfDate();

	@Schema(description = "The date of email sent")
	@Nullable
	Date getEmailSentDate();

	@Schema(description = "The payement status", example = "possible value are : <ul>"
			+ "<li>NONE : invoice has no payment status, no AO created.</li>"
			+ "<li>PENDING : AO created, due date is still in the future</li>"
			+ "<li>PAID : invoice has no payment status, no AO created.</li>"
			+ "<li>PPAID : invoice has no payment status, no AO created</li>"
			+ "<li>UNPAID : invoice has no payment status, no AO created.</li>"
			+ "<li>ABANDONED : invoice has no payment status, no AO created.</li>"
			+ "<li>REFUNDED : invoice has no payment status, no AO created.</li>"
			+ "<li>DISPUTED : invoice has no payment status, no AO created.</li>")
	@Nullable
	InvoicePaymentStatusEnum getPaymentStatus();

	@Schema(description = "The date of the payment status")
	@Nullable
	Date getPaymentStatusDate();

	@Schema(description = "The start date")
	@Nullable
	Date getStartDate();

	@Schema(description = "The end date")
	@Nullable
	Date getEndDate();

	@Schema(description = "The raw amount")
	@Nullable
	BigDecimal getRawAmount();

	@Schema(description = "The discount rate")
	@Nullable
	BigDecimal getDiscountRate();

	@Schema(description = "The discount amount")
	@Nullable
	BigDecimal getDiscountAmount();

	@Schema(description = "Indicate if the invoice is already applied minumun")
	@Nullable
	@JsonProperty("alreadyAppliedMinimum")
	Boolean isAlreadyAppliedMinimum();

	@Schema(description = "Indicate if the invoice is discount already added")
	@Nullable
	@JsonProperty("alreadyAddedDiscount")
	Boolean isAlreadyAddedDiscount();

	@Schema(description = "The invoice adjustment current seller")
	@Nullable
	Long getInvoiceAdjustmentCurrentSellerNb();

	@Schema(description = "The invoice adjustment current provider")
	@Nullable
	Long getInvoiceAdjustmentCurrentProviderNb();

	@Schema(description = "The previous invoice number")
	@Nullable
	String getPreviousInvoiceNumber();

	@Schema(description = "Indicate if the invoice is draft")
	@Nullable
	@JsonProperty("draft")
	Boolean getDraft();

	@Schema(description = "The description of the invoice")
	@Nullable
	String getDescription();

	@Schema(description = "The list of the invoice lines")
	@Nullable
	List<InvoiceLine> getInvoiceLines();

	@Schema(description = "The list of linked invoices")
	@Nullable
	List<Long> getListLinkedInvoices();

	@Schema(description = "The list of the caregory invoice agregates")
	@Nullable
	List<CategoryInvoiceAgregate> getCategoryInvoiceAgregates();

	@Schema(description = "The list of the invoice lines to link")
	@Nullable
	List<Long> getInvoiceLinesTolink();

	@Schema(description = "The discount plan attached to this invoice")
	@Nullable
	Resource getDiscountPlan();

	@Schema(description = "The order attached to this invoice")
	@Nullable
	Resource getCommercialOrder();
	
	@Schema(description = "The cpqQuote attached to this invoice")
	@Nullable
	Resource getCpqQuote();
	
	@Schema(description = "The temporary invoice number")
	@Nullable
	String getTemporaryInvoiceNumber();

	@Schema(description = "Amount without tax before discount")
	@Nullable
	BigDecimal getAmountWithoutTaxBeforeDiscount();

	@Schema(description = "discount amount without tax")
	@Nullable
	BigDecimal getDiscountAmountWithoutTax();

	@Nullable
	@Schema(description = "custom field associated to invoice")
	CustomFieldsDto getCustomFields();

	@Schema(description = "The flag for auto matching")
	@Nullable
	Boolean getAutoMatching();
	
	@Schema(description = "The external purchase order number")
	@Nullable
	String getPurchaseOrder();
}