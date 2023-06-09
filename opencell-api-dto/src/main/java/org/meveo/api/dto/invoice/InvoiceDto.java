/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.dto.invoice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.AuditableEntityDto;
import org.meveo.api.dto.CategoryInvoiceAgregateDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.TaxInvoiceAggregateDto;
import org.meveo.api.dto.payment.PaymentScheduleInstancesDto;
import org.meveo.api.dto.payment.RecordedInvoiceDto;
import org.meveo.model.billing.InvoiceModeEnum;
import org.meveo.model.billing.InvoicePaymentStatusEnum;
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.model.payments.PaymentMethodEnum;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO equivalent of Invoice entity.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "Invoice")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceDto extends AuditableEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1072382628068718580L;

    /** The invoice id. */
    @Schema(description = "The invoice id")
    protected Long invoiceId;

    /** The invoice type. */
    @XmlElement(required = true)
    @Schema(description = "The invoice type")
    protected String invoiceType;

    /** The billing account code. */
    @XmlElement(required = true)
    @Schema(description = "The billing account code")
    protected String billingAccountCode;

    /**
     * Code of the Seller
     */
    @Schema(description = "Code of the Seller")
    protected String sellerCode;

    /**
     * Code of the subscription
     */
    @Schema(description = "Code of the subscription")
    protected String subscriptionCode;

    /**
     * Id of the subscription
     */
    @Schema(description = "Id of the subscription")
    protected Long subscriptionId;

    /**
     * Order number
     */
    @Schema(description = "Order number of the invoice")
    protected String orderNumber;

    /** The invoice status. */
    @Schema(description = "The invoice status", example = "possible value are : NEW, SUSPECT, REJECTED, DRAFT, CANCELED, VALIDATED")
    private InvoiceStatusEnum status;

    /** The due date. */
    @XmlElement(required = true)
    @Schema(description = "The due date")
    protected Date dueDate;

    /** The invoice date. */
    @XmlElement(required = true)
    @Schema(description = "The invoice date")
    protected Date invoiceDate;

    /** The category invoice aggregates. */
    @XmlElementWrapper
    @XmlElement(name = "categoryInvoiceAgregate")
    @Schema(description = "The category invoice aggregates")
    protected List<CategoryInvoiceAgregateDto> categoryInvoiceAgregates = new ArrayList<CategoryInvoiceAgregateDto>();

    /** The tax aggregates */
    @XmlElementWrapper
    @XmlElement(name = "taxAggregate", required = true)
    @Schema(description = "The tax aggregates")
    protected List<TaxInvoiceAggregateDto> taxAggregates = new ArrayList<TaxInvoiceAggregateDto>();

    /** The list invoice id to link. */
    @XmlElementWrapper
    @XmlElement(name = "invoiceIdToLink")
    @Schema(description = "The list invoice id to link")
    protected List<Long> listInvoiceIdToLink = new ArrayList<Long>();

    /** The invoice number. */
    @Schema(description = "The invoice number")
    protected String invoiceNumber;

    /** The discount. */
    @Schema(description = "discount applied to this invoice")
    protected BigDecimal discount;

    /** The amount without tax. */
    @Schema(description = "The amount without tax")
    protected BigDecimal amountWithoutTax;

    /** The amount tax. */
    @Schema(description = "The amount tax")
    protected BigDecimal amountTax;

    /** The amount with tax. */
    @Schema(description = "The amount with tax")
    protected BigDecimal amountWithTax;

    /** The payment method. */
    @Schema(description = "The payment method", example = "possible value are : CHECK, DIRECTDEBIT, WIRETRANSFER, CARD, PAYPAL, STRIPE, CASH")
    protected PaymentMethodEnum paymentMethod;

    /** The xml filename. */
    @Schema(description = "The xml filename")
    protected String xmlFilename;

    /** The xml. */
    @Schema(description = "The xml")
    protected String xml;

    /** The pdf filename. */
    @Schema(description = "he pdf filename")
    protected String pdfFilename;

    /** The pdf. */
    @Schema(description = "content of the pdf")
    protected byte[] pdf;

    /**
     * A request-only parameter. True if invoice should be assigned a number. Defaults to True.
     */
    @Schema(description = "A request-only parameter. True if invoice should be assigned a number", defaultValue = "true")
    protected Boolean autoValidation;

    /**
     * A request-only parameter. True if XML invoice should be generated and returned.
     */
    @Schema(description = "A request-only parameter. True if XML invoice should be generated and returned")
    protected Boolean returnXml;

    /**
     * A request-only parameter. True if PDF invoice should be generated and returned.
     */
    @Schema(description = "A request-only parameter. True if PDF invoice should be generated and returned")
    protected Boolean returnPdf;

    /**
     * A request-only parameter. True if PDF should be delivered by email. Defaults to True.
     */
    @Schema(description = "A request-only parameter. True if PDF should be delivered by email. Defaults to True")
    private Boolean sendByEmail;

    /**
     * A request-only parameter. True if currently due balance should be returned
     */
    @Schema(description = "A request-only parameter. True if currently due balance should be returned")
    protected Boolean includeBalance;

    /** The recorded invoice dto. */
    @Schema(description = "The recorded invoice dto")
    protected RecordedInvoiceDto recordedInvoiceDto;

    /** The net to pay. */
    @Schema(description = "The net to pay")
    protected BigDecimal netToPay;

    /** The invoice mode. */
    @XmlElement(required = true)
    @Schema(description = "The invoice mode", example = "possible value are: AGGREGATED, DETAILLED")
    protected InvoiceModeEnum invoiceMode;

    /** The custom fields. */
    @Schema(description = "")
    protected CustomFieldsDto customFields;

    /**
     * The total due is a snapshot at invoice generation time of the due balance (not exigible) before invoice calculation+invoice amount. Due balance is a "future" dueBalance (the
     * due balance at the invoice due date).
     */
    @Schema(description = "The total due is a snapshot at invoice generation time of the due balance (not exigible) before invoice calculation+invoice amount. Due balance is a 'future' dueBalance (the due balance at the invoice due date)")
    protected BigDecimal dueBalance;

    /**
     * A flag to generate a draft invoice
     */
    @Schema(description = "A flag to generate a draft invoice")
    protected Boolean isDraft;

    /**
     * Before sending the invoice, check if not already sent
     */
    @Schema(description = "Before sending the invoice, check if not already sent")
    protected boolean checkAlreadySent;

    /**
     * Override Email defined in the billing entity
     */
    @Schema(description = "Override Email defined in the billing entity")
    protected String overrideEmail;

    /**
     * True if the invoice was sent by email or delibered by some other electronic means, false otherwise
     */
    @Schema(description = "True if the invoice was sent by email or delibered by some other electronic means, false otherwise", defaultValue = "false")
    protected boolean sentByEmail;

    /**
     * list of related payment schedule instances
     *
     */
    @Schema(description = "list of related payment schedule instances")
    protected PaymentScheduleInstancesDto paymentScheduleInstancesDto;

    /**
     * associated dunning creation date
     *
     */
    @Schema(description = "associated dunning creation date")
    protected Date dunningEntryDate;

    /**
     * associated dunning last update date
     *
     */
    @Schema(description = "associated dunning last update date")
    protected Date dunningLastModification;

    /**
     * associated dunning current status
     *
     */
    @Schema(description = "associated dunning current status")
    protected String dunningStatus;

    /**
     * The invoice real time status.
     */
    @Schema(description = "The invoice real time status", example = "possible value are: NONE, PENDING, PAID, PPAID, UNPAID, ABANDONED, REFUNDED, DISPUTED")
    private InvoicePaymentStatusEnum realTimeStatus;

    /**
     * list of existing RTs to include, identified by id This option is allowed only if invoiceMode=="DETAILLED"
     *
     */
    @Schema(description = "List of existing RTs to include, identified by id This option is allowed only if invoiceMode=='DETAILLED'")
    protected List<Long> ratedTransactionsToLink;
    /**
     * paymentIncident
     *
     */
    @Schema(description = "List of payment incidents")
    protected List<String> paymentIncidents;

    /**
     * sendPaymentDate
     *
     */
    @Schema(description = "Date of send payment")
    protected Date sendPaymentDate;

    /**
     * Invoice payment collection date.
     */
    @Schema(description = "Invoice payment collection date")
    private Date initialCollectionDate;
    /**
     * sum off writeOff accountOperations amounts
     */
    @Schema(description = "Sum off writeOff accountOperations amounts")
    protected BigDecimal writeOffAmount;

    /**
     * last payment Date
     */
    @Schema(description = "last payment Date")
    protected Date paymentDate;


    /**
     * Invoice status change date
     */
    @Schema(description = "Invoice status change date")
    protected Date statusDate;

    /**
     * Date when the XML has been produced on a validated invoice.
     */
    @Schema(description = "Date when the XML has been produced on a validated invoice")
    protected Date xmlDate;

    /**
     * Date when the PDf has been produced on a validated invoice.
     */
    @Schema(description = "Date when the PDf has been produced on a validated invoice")
    protected Date pdfDate;

    /**
     * Date when the invoice has been sent for a validated invoice
     */
    @Schema(description = "Date when the invoice has been sent for a validated invoice")
    protected Date emailSentDate;

    /**
     *
     */
    @Schema(description = "payment status", example = "possible value are: NONE, PENDING, PAID, PPAID, UNPAID, ABANDONED, REFUNDED, DISPUTED")
	protected InvoicePaymentStatusEnum paymentStatus;

    /**
     * Payment status change date
     */
    @Schema(description = "Payment status change date")
	protected Date paymentStatusDate;

    /**
     * Beginning of the billed period (based on billing cycle period whenever possible or min(invoiceLine.valueDate))
     */
    @Schema(description = "Beginning of the billed period (based on billing cycle period whenever possible or min(invoiceLine.valueDate))")
    protected Date startDate;


    /**
     * End of the billed period (based on billing cycle period whenever possible or applied lastTransactionDate or max(invoiceLine.valueDate))
     */
    @Schema(description = "End of the billed period (based on billing cycle period whenever possible or applied lastTransactionDate or max(invoiceLine.valueDate))")
    protected Date endDate;
     

    /**
     * Total raw amount from invoice lines.
     *      -Does not include discount.
     *      -With or without tax depending on provider setting (isEnterprise).
     */
    @XmlElement(required = true)
    @Schema(description = "Total raw amount from invoice lines. <ul><li>Does not include discount</li><li>With or without tax depending on provider setting (isEnterprise)</li></ul>")
    protected BigDecimal rawAmount;

    /**
     * Discount rate to apply (in %).
     * Initialize with discount rate from linked invoice discount plan.
     */
    @Schema(description = "Discount rate to apply (in %).<p>Initialize with discount rate from linked invoice discount plan</p>")
    protected BigDecimal discountRate;

	/**
     * Total discount amount with or without tax depending on provider settings.
	 * Can be inconsistent with discountRate.
	 * discountAmount has precedence over discountRate
     */
    @XmlElement(required = true)
    @Schema(description = "Total discount amount with or without tax depending on provider settings.<p>Can be inconsistent with discountRate.</p><p>discountAmount has precedence over discountRate</p>")
    protected BigDecimal discountAmount=BigDecimal.ZERO;

    /**
     * Indicates if the invoicing minimum has already been applied
     */
    @Schema(description = "Indicates if the invoicing minimum has already been applied")
    protected boolean isAlreadyAppliedMinimum;

    /**
     * Indicates if the invoice discounts have already been applied
     */
    @Schema(description = "Indicates if the invoice discounts have already been applied")
    protected boolean isAlreadyAddedDiscount;
    
    /**
     * Discount plan code
     */
    @XmlElement
    @Schema(description = "Discount plan code")
    private String discountPlanCode;
    
    /**
     * The exchange rate that converted amounts of the invoice.
     */
    @Schema(description = "The exchange rate that converted amounts of the invoice.")
    private BigDecimal lastAppliedRate;
    
    /**
     * The date of exchange rate applied to amounts of the invoice.
     */
    @Schema(description = "The date of exchange rate applied to amounts of the invoice.")
    private Date lastAppliedRateDate;

    @Schema
    protected boolean autoMatching;

    public List<String> getPaymentIncidents() {
        return paymentIncidents;
    }

    public void setPaymentIncidents(List<String> paymentIncidents) {
        this.paymentIncidents = paymentIncidents;
    }

    public void addPaymentIncidents(String paymentIncident) {
        this.paymentIncidents.add(paymentIncident);
    }

    public BigDecimal getWriteOffAmount() {
        return writeOffAmount;
    }

    public void setWriteOffAmount(BigDecimal writeOffAmount) {
        this.writeOffAmount = writeOffAmount;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    /**
     * Get the list of existing RTs to include.
     *
     * @return the ratedTransactionsTolink
     */
    public List<Long> getRatedTransactionsTolink() {
        return ratedTransactionsToLink;
    }

    /**
     * Set the list of existing RTs to include.
     *
     * @param the ratedTransactionTolink
     */
    public void setRatedTransactionsTolink(List<Long> ratedTransactionsTolink) {
        this.ratedTransactionsToLink = ratedTransactionsTolink;
    }

    /**
     * Gets the list invoice id to link.
     *
     * @return the listInvoiceIdToLink
     */
    public List<Long> getListInvoiceIdToLink() {
        return listInvoiceIdToLink;
    }

    /**
     * Sets the list invoice id to link.
     *
     * @param listInvoiceIdToLink the listInvoiceIdToLink to set
     */
    public void setListInvoiceIdToLink(List<Long> listInvoiceIdToLink) {
        this.listInvoiceIdToLink = listInvoiceIdToLink;
    }

    /**
     * Gets the billing account code.
     *
     * @return the billing account code
     */
    public String getBillingAccountCode() {
        return billingAccountCode;
    }

    /**
     * Sets the billing account code.
     *
     * @param billingAccountCode the new billing account code
     */
    public void setBillingAccountCode(String billingAccountCode) {
        this.billingAccountCode = billingAccountCode;
    }

    /**
     * Gets the invoice date.
     *
     * @return the invoice date
     */
    public Date getInvoiceDate() {
        return invoiceDate;
    }

    /**
     * Sets the invoice date.
     *
     * @param invoiceDate the new invoice date
     */
    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    /**
     * Gets the due date.
     *
     * @return the due date
     */
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * Sets the due date.
     *
     * @param dueDate the new due date
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Gets the discount.
     *
     * @return the discount
     */
    public BigDecimal getDiscount() {
        return discount;
    }

    /**
     * Sets the discount.
     *
     * @param discount the new discount
     */
    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    /**
     * Gets the amount without tax.
     *
     * @return the amount without tax
     */
    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    /**
     * Sets the amount without tax.
     *
     * @param amountWithoutTax the new amount without tax
     */
    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    /**
     * Gets the amount tax.
     *
     * @return the amount tax
     */
    public BigDecimal getAmountTax() {
        return amountTax;
    }

    /**
     * Sets the amount tax.
     *
     * @param amountTax the new amount tax
     */
    public void setAmountTax(BigDecimal amountTax) {
        this.amountTax = amountTax;
    }

    /**
     * Gets the amount with tax.
     *
     * @return the amount with tax
     */
    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    /**
     * Sets the amount with tax.
     *
     * @param amountWithTax the new amount with tax
     */
    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    /**
     * Gets the xml filename.
     *
     * @return the xml filename
     */
    public String getXmlFilename() {
        return xmlFilename;
    }

    /**
     * Sets the xml filename.
     *
     * @param xmlFilename the new xml filename
     */
    public void setXmlFilename(String xmlFilename) {
        this.xmlFilename = xmlFilename;
    }

    /**
     * Gets the pdf filename.
     *
     * @return the pdf filename
     */
    public String getPdfFilename() {
        return pdfFilename;
    }

    /**
     * Sets the pdf filename.
     *
     * @param pdfFilename the new pdf filename
     */
    public void setPdfFilename(String pdfFilename) {
        this.pdfFilename = pdfFilename;
    }

    /**
     * Gets the payment method.
     *
     * @return the payment method
     */
    public PaymentMethodEnum getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Sets the payment method.
     *
     * @param paymentMethod the new payment method
     */
    public void setPaymentMethod(PaymentMethodEnum paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     * Gets the invoice id.
     *
     * @return the invoiceId
     */
    public Long getInvoiceId() {
        return invoiceId;
    }

    /**
     * Sets the invoice id.
     *
     * @param invoiceId id of invoice.
     */
    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    /**
     * Gets the invoice type.
     *
     * @return the invoiceType
     */
    public String getInvoiceType() {
        return invoiceType;
    }

    /**
     * Sets the invoice type.
     *
     * @param invoiceType the invoiceType to set
     */
    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    /**
     * Gets the pdf.
     *
     * @return the pdf
     */
    public byte[] getPdf() {
        return pdf;
    }

    /**
     * Sets the pdf.
     *
     * @param pdf the new pdf
     */
    public void setPdf(byte[] pdf) {
        this.pdf = pdf;
    }

    /**
     * Gets the custom fields.
     *
     * @return the customFields
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * Sets the custom fields.
     *
     * @param customFields the customFields to set
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    /**
     * Gets the category invoice agregates.
     *
     * @return the categoryInvoiceAgregates
     */
    public List<CategoryInvoiceAgregateDto> getCategoryInvoiceAgregates() {
        return categoryInvoiceAgregates == null ? new ArrayList<>() : categoryInvoiceAgregates;
    }

    /**
     * Sets the category invoice agregates.
     *
     * @param categoryInvoiceAgregates the categoryInvoiceAgregates to set
     */
    public void setCategoryInvoiceAgregates(List<CategoryInvoiceAgregateDto> categoryInvoiceAgregates) {
        this.categoryInvoiceAgregates = categoryInvoiceAgregates;
    }

    /**
     * A request-only parameter
     *
     * @return True if invoice should be assigned a number
     */
    public Boolean isAutoValidation() {
        return autoValidation;
    }

    /**
     * A request-only parameter
     *
     * @param autoValidation True if invoice should be assigned a number
     */
    public void setAutoValidation(Boolean autoValidation) {
        this.autoValidation = autoValidation;
    }

    /**
     * A request-only parameter
     * 
     * @return True if XML invoice content should be returned
     */
    public Boolean isReturnXml() {
        return returnXml;
    }

    /**
     * A request-only parameter
     *
     * @param returnXml True if XML invoice content should be returned
     */
    public void setReturnXml(Boolean returnXml) {
        this.returnXml = returnXml;
    }

    /**
     * A request-only parameter
     *
     * @return True if PDF invoice content should be returned
     */
    public Boolean isReturnPdf() {
        return returnPdf;
    }

    /**
     * A request-only parameter
     *
     * @param returnPdf True if PDF invoice content should be returned
     */
    public void setReturnPdf(Boolean returnPdf) {
        this.returnPdf = returnPdf;
    }

    /**
     * @return A request-only parameter. True if PDF should be delivered by email. Defaults to True.
     */
    public Boolean getSendByEmail() {
        return sendByEmail;
    }

    /**
     * @param sendByEmail A request-only parameter. True if PDF should be delivered by email. Defaults to True.
     */
    public void setSendByEmail(Boolean sendByEmail) {
        this.sendByEmail = sendByEmail;
    }

    /**
     * Gets the invoice mode.
     *
     * @return the invoiceMode
     */
    public InvoiceModeEnum getInvoiceMode() {
        return invoiceMode;
    }

    /**
     * Sets the invoice mode.
     *
     * @param invoiceMode the invoiceMode to set
     */
    public void setInvoiceMode(InvoiceModeEnum invoiceMode) {
        this.invoiceMode = invoiceMode;
    }

    /**
     * Gets the invoice number.
     *
     * @return the invoiceNumber
     */
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * Sets the invoice number.
     *
     * @param invoiceNumber the invoiceNumber to set
     */
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    /**
     * A request-only parameter
     *
     * @return True if currently due balance should be returned
     */
    public Boolean isIncludeBalance() {
        return includeBalance;
    }

    /**
     * A request-only parameter
     *
     * @param includeBalance True if currently due balance should be returned
     */
    public void setIncludeBalance(Boolean includeBalance) {
        this.includeBalance = includeBalance;
    }

    /**
     * Gets the recorded invoice dto.
     *
     * @return the recordedInvoiceDto
     */
    public RecordedInvoiceDto getRecordedInvoiceDto() {
        return recordedInvoiceDto;
    }

    /**
     * Sets the recorded invoice dto.
     *
     * @param recordedInvoiceDto the recordedInvoiceDto to set
     */
    public void setRecordedInvoiceDto(RecordedInvoiceDto recordedInvoiceDto) {
        this.recordedInvoiceDto = recordedInvoiceDto;
    }

    /**
     * @return Net amount to pay = amountWithTax+dueBalance
     */
    public BigDecimal getNetToPay() {
        return netToPay;
    }

    /**
     * @param netToPay Net amount to pay = amountWithTax+dueBalance
     */
    public void setNetToPay(BigDecimal netToPay) {
        this.netToPay = netToPay;
    }

    /**
     * @return XML invoice contents
     */
    public String getXml() {
        return xml;
    }

    /**
     * @param xml XML invoice contents
     */
    public void setXml(String xml) {
        this.xml = xml;
    }

    /**
     * @return Currently due balance
     */
    public BigDecimal getDueBalance() {
        return dueBalance;
    }

    /**
     * @param dueBalance Currently due balance
     */
    public void setDueBalance(BigDecimal dueBalance) {
        this.dueBalance = dueBalance;
    }

    /**
     * @return Tax aggregates
     */
    public List<TaxInvoiceAggregateDto> getTaxAggregates() {
        return taxAggregates;
    }

    /**
     * @param taxAggregates Tax aggregates
     */
    public void setTaxAggregates(List<TaxInvoiceAggregateDto> taxAggregates) {
        this.taxAggregates = taxAggregates;
    }

    /**
     * @return Subscription code
     */
    public String getSubscriptionCode() {
        return subscriptionCode;
    }

    /**
     * @param subscriptionCode Subscription code
     */
    public void setSubscriptionCode(String subscriptionCode) {
        this.subscriptionCode = subscriptionCode;
    }

    /**
     * @return Order number
     */
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * @param orderNumber Order number
     */
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * @return Seller's code
     */
    public String getSellerCode() {
        return sellerCode;
    }

    /**
     * @param sellerCode Seller's code
     */
    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
    }

    /**
     * @return the status
     */
    public InvoiceStatusEnum getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(InvoiceStatusEnum status) {
        this.status = status;
    }

    public Boolean isDraft() {
        if (isDraft == null) {
            isDraft = Boolean.FALSE;
        }
        return isDraft;
    }

    public void setDraft(Boolean draft) {
        this.isDraft = draft;
    }

    public boolean isCheckAlreadySent() {
        return checkAlreadySent;
    }

    public void setCheckAlreadySent(boolean checkAlreadySent) {
        this.checkAlreadySent = checkAlreadySent;
    }

    public String getOverrideEmail() {
        return overrideEmail;
    }

    public void setOverrideEmail(String overrideEmail) {
        this.overrideEmail = overrideEmail;
    }

    public boolean isSentByEmail() {
        return sentByEmail;
    }

    public void setSentByEmail(boolean sentByEmail) {
        this.sentByEmail = sentByEmail;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Date getDunningEntryDate() {
        return dunningEntryDate;
    }

    public void setDunningEntryDate(Date dunningEntryDate) {
        this.dunningEntryDate = dunningEntryDate;
    }

    public Date getDunningLastModification() {
        return dunningLastModification;
    }

    public void setDunningLastModification(Date dunningLastModification) {
        this.dunningLastModification = dunningLastModification;
    }

    public String getDunningStatus() {
        return dunningStatus;
    }

    public void setDunningStatus(String dunningStatus) {
        this.dunningStatus = dunningStatus;
    }

    public PaymentScheduleInstancesDto getPaymentScheduleInstancesDto() {
        return paymentScheduleInstancesDto;
    }

    public void setPaymentScheduleInstancesDto(PaymentScheduleInstancesDto paymentScheduleInstancesDto) {
        this.paymentScheduleInstancesDto = paymentScheduleInstancesDto;
    }

    /**
     * @return the realTimeStatus
     */
    public InvoicePaymentStatusEnum getRealTimeStatus() {
        return realTimeStatus;
    }

    /**
     * @param realTimeStatus the realTimeStatus to set
     */
    public void setRealTimeStatus(InvoicePaymentStatusEnum realTimeStatus) {
        this.realTimeStatus = realTimeStatus;
    }

	public Boolean getIsDraft() {
		return isDraft;
	}

	public void setIsDraft(Boolean isDraft) {
		this.isDraft = isDraft;
	}

	public List<Long> getRatedTransactionsToLink() {
		return ratedTransactionsToLink;
	}

	public void setRatedTransactionsToLink(List<Long> ratedTransactionsToLink) {
		this.ratedTransactionsToLink = ratedTransactionsToLink;
	}

	public Date getSendPaymentDate() {
		return sendPaymentDate;
	}

	public void setSendPaymentDate(Date sendPaymentDate) {
		this.sendPaymentDate = sendPaymentDate;
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public Date getXmlDate() {
		return xmlDate;
	}

	public void setXmlDate(Date xmlDate) {
		this.xmlDate = xmlDate;
	}

	public Date getPdfDate() {
		return pdfDate;
	}

	public void setPdfDate(Date pdfDate) {
		this.pdfDate = pdfDate;
	}

	public Date getEmailSentDate() {
		return emailSentDate;
	}

	public void setEmailSentDate(Date emailSentDate) {
		this.emailSentDate = emailSentDate;
	}

	public InvoicePaymentStatusEnum getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(InvoicePaymentStatusEnum paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public Date getPaymentStatusDate() {
		return paymentStatusDate;
	}

	public void setPaymentStatusDate(Date paymentStatusDate) {
		this.paymentStatusDate = paymentStatusDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public BigDecimal getRawAmount() {
		return rawAmount;
	}

	public void setRawAmount(BigDecimal rawAmount) {
		this.rawAmount = rawAmount;
	}

	public BigDecimal getDiscountRate() {
		return discountRate;
	}

	public void setDiscountRate(BigDecimal discountRate) {
		this.discountRate = discountRate;
	}

	public BigDecimal getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(BigDecimal discountAmount) {
		this.discountAmount = discountAmount;
	}

	public boolean isAlreadyAppliedMinimum() {
		return isAlreadyAppliedMinimum;
	}

	public void setAlreadyAppliedMinimum(boolean isAlreadyAppliedMinimum) {
		this.isAlreadyAppliedMinimum = isAlreadyAppliedMinimum;
	}

	public boolean isAlreadyAddedDiscount() {
		return isAlreadyAddedDiscount;
	}

	public void setAlreadyAddedDiscount(boolean isAlreadyAddedDiscount) {
		this.isAlreadyAddedDiscount = isAlreadyAddedDiscount;
	}

	public Boolean getAutoValidation() {
		return autoValidation;
	}

	public Boolean getReturnXml() {
		return returnXml;
	}

	public Boolean getReturnPdf() {
		return returnPdf;
	}

	public Boolean getIncludeBalance() {
		return includeBalance;
	}

    public Date getInitialCollectionDate() {
        return initialCollectionDate;
    }

    public void setInitialCollectionDate(Date intialCollectionDate) {
        this.initialCollectionDate = intialCollectionDate;
    }

	public String getDiscountPlanCode() {
		return discountPlanCode;
	}

	public void setDiscountPlanCode(String discountPlanCode) {
		this.discountPlanCode = discountPlanCode;
	}

    /**
     * @return the lastAppliedRate
     */
    public BigDecimal getLastAppliedRate() {
        return lastAppliedRate;
    }

    /**
     * @param lastAppliedRate the lastAppliedRate to set
     */
    public void setLastAppliedRate(BigDecimal lastAppliedRate) {
        this.lastAppliedRate = lastAppliedRate;
    }

    /**
     * @return the lastAppliedRateDate
     */
    public Date getLastAppliedRateDate() {
        return lastAppliedRateDate;
    }

    /**
     * @param lastAppliedRateDate the lastAppliedRateDate to set
     */
    public void setLastAppliedRateDate(Date lastAppliedRateDate) {
        this.lastAppliedRateDate = lastAppliedRateDate;
    }

    public boolean isAutoMatching() {
        return autoMatching;
    }

    public void setAutoMatching(boolean autoMatching) {
        this.autoMatching = autoMatching;
    }
}