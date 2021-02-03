package org.meveo.apiv2.billing;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.billing.InvoicePaymentStatusEnum;
import org.meveo.model.billing.InvoiceStatusEnum;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableInvoice.class)
public interface Invoice extends Resource {

	Resource getBillingAccount();

	@Nullable
	Resource getBillingRun();

	@Nullable
	Resource getRecordedInvoice();

	@Nullable
	String getInvoiceNumber();

	@Nullable
	Date getProductDate();

	@Nullable
	Date getInvoiceDate();

	InvoiceStatusEnum getStatus();

	@Nullable
	Date getDueDate();

	@Nullable
	BigDecimal getAmount();

	@Nullable
	BigDecimal getDiscount();

	BigDecimal getAmountWithoutTax();

	BigDecimal getAmountTax();

	BigDecimal getAmountWithTax();

	BigDecimal getNetToPay();

	@Nullable
	Resource getPaymentMethodType();

	@Nullable
	String getIban();

	@Nullable
	String getAlias();

	@Nullable
	Resource getTradingCurrency();

	@Nullable
	Resource getTradingCountry();

	@Nullable
	Resource getTradingLanguage();

	@Nullable
	String getComment();

	@Nullable
	@JsonProperty("detailedInvoice")
	Boolean isDetailedInvoice();

	@Nullable
	Resource getAdjustedInvoice();

	Resource getInvoiceType();

	@Nullable
	Resource getCfValues();

	@Nullable
	Resource getCfAccumulatedValues();

	@Nullable
	Resource getQuote();

	@Nullable
	Resource getSubscription();

	@Nullable
	Resource getOrder();

	@Nullable
	String getXmlFilename();

	@Nullable
	String getPdfFilename();

	@Nullable
	Resource getPaymentMethod();

	@Nullable
	BigDecimal getDueBalance();

	@Nullable
	@JsonProperty("alreadySent")
	Boolean isAlreadySent();

	@Nullable
	@JsonProperty("dontSend")
	Boolean isDontSend();

	Resource getSeller();

	@Nullable
	@JsonProperty("prepaid")
	Boolean isPrepaid();

	@Nullable
	String getExternalRef();

	@Nullable
	String getRejectReason();

	@Nullable
	Date getInitialCollectionDate();

	@Nullable
	Date getStatusDate();

	@Nullable
	Date getXmlDate();

	@Nullable
	Date getPdfDate();

	@Nullable
	Date getEmailSentDate();

	InvoicePaymentStatusEnum getPaymentStatus();

	@Nullable
	Date getPaymentStatusDate();

	@Nullable
	Date getStartDate();

	@Nullable
	Date getEndDate();

	BigDecimal getRawAmount();

	@Nullable
	BigDecimal getDiscountRate();

	BigDecimal getDiscountAmount();

	@Nullable
	@JsonProperty("alreadyAppliedMinimum")
	Boolean isAlreadyAppliedMinimum();

	@Nullable
	@JsonProperty("alreadyAddedDiscount")
	Boolean isAlreadyAddedDiscount();

	@Nullable
	Long getInvoiceAdjustmentCurrentSellerNb();

	@Nullable
	Long getInvoiceAdjustmentCurrentProviderNb();

	@Nullable
	String getPreviousInvoiceNumber();

	@Nullable
	@JsonProperty("draft")
	Boolean getDraft();

	String getCode();

	@Nullable
	String getDescription();

}
