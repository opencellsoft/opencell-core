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
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.model.payments.PaymentMethodEnum;

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
    protected Long invoiceId;

    /** The invoice type. */
    @XmlElement(required = true)
    protected String invoiceType;

    /** The billing account code. */
    @XmlElement(required = true)
    protected String billingAccountCode;

    /**
     * Code of the Seller
     */
    protected String sellerCode;

    /**
     * Code of the subscription
     */
    protected String subscriptionCode;

    /**
     * Id of the subscription
     */
    protected Long subscriptionId;

    /**
     * Order number
     */
    protected String orderNumber;

    /** The invoice status. */
    private InvoiceStatusEnum status;

    /** The due date. */
    @XmlElement(required = true)
    protected Date dueDate;

    /** The invoice date. */
    @XmlElement(required = true)
    protected Date invoiceDate;

    /** The category invoice aggregates. */
    @XmlElementWrapper
    @XmlElement(name = "categoryInvoiceAgregate")
    protected List<CategoryInvoiceAgregateDto> categoryInvoiceAgregates = new ArrayList<CategoryInvoiceAgregateDto>();

    /** The tax aggregates */
    @XmlElementWrapper
    @XmlElement(name = "taxAggregate", required = true)
    protected List<TaxInvoiceAggregateDto> taxAggregates = new ArrayList<TaxInvoiceAggregateDto>();

    /** The list invoice id to link. */
    @XmlElementWrapper
    @XmlElement(name = "invoiceIdToLink")
    protected List<Long> listInvoiceIdToLink = new ArrayList<Long>();

    /** The invoice number. */
    protected String invoiceNumber;

    /** The discount. */
    protected BigDecimal discount;

    /** The amount without tax. */
    protected BigDecimal amountWithoutTax;

    /** The amount tax. */
    protected BigDecimal amountTax;

    /** The amount with tax. */
    protected BigDecimal amountWithTax;

    /** The payment method. */
    protected PaymentMethodEnum paymentMethod;

    /** The xml filename. */
    protected String xmlFilename;

    /** The xml. */
    protected String xml;

    /** The pdf filename. */
    protected String pdfFilename;

    /** The pdf. */
    protected byte[] pdf;

    /**
     * A request-only parameter. True if invoice should be assigned a number. Defaults to True.
     */
    protected Boolean autoValidation;

    /**
     * A request-only parameter. True if XML invoice should be generated and returned.
     */
    protected Boolean returnXml;

    /**
     * A request-only parameter. True if PDF invoice should be generated and returned.
     */
    protected Boolean returnPdf;

    /**
     * A request-only parameter. True if PDF should be delivered by email. Defaults to True.
     */
    private Boolean sendByEmail;

    /**
     * A request-only parameter. True if currently due balance should be returned
     */
    protected Boolean includeBalance;

    /** The recorded invoice dto. */
    protected RecordedInvoiceDto recordedInvoiceDto;

    /** The net to pay. */
    protected BigDecimal netToPay;

    /** The invoice mode. */
    @XmlElement(required = true)
    protected InvoiceModeEnum invoiceMode;

    /** The custom fields. */
    protected CustomFieldsDto customFields;

    /**
     * The total due is a snapshot at invoice generation time of the due balance (not exigible) before invoice calculation+invoice amount. Due balance is a "future" dueBalance (the
     * due balance at the invoice due date).
     */
    protected BigDecimal dueBalance;

    /**
     * A flag to generate a draft invoice
     */
    protected Boolean isDraft;

    /**
     * Before sending the invoice, check if not already sent
     */
    protected boolean checkAlreadySent;

    /**
     * Override Email defined in the billing entity
     */
    protected String overrideEmail;

    /**
     * True if the invoice was sent by email or delibered by some other electronic means, false otherwise
     */
    protected boolean sentByEmail;

    /**
     * list of related payment schedule instances
     *
     */
    protected PaymentScheduleInstancesDto paymentScheduleInstancesDto;

    /**
     * associated dunning creation date
     *
     */
    protected Date dunningEntryDate;

    /**
     * associated dunning last update date
     *
     */
    protected Date dunningLastModification;

    /**
     * associated dunning current status
     *
     */
    protected String dunningStatus;

    /**
     * The invoice real time status.
     */
    private InvoiceStatusEnum realTimeStatus;

    /**
     * list of existing RTs to include, identified by id This option is allowed only if invoiceMode=="DETAILLED"
     * 
     */
    protected List<Long> ratedTransactionsToLink;
    /**
     * paymentIncident
     *
     */
    protected List<String> paymentIncidents;

    /**
     * sendPaymentDate
     *
     */
    protected Date sendPaymentDate;

    /**
     * Invoice payment collection date.
     */
    private Date intialCollectionDate;
    /**
     * sum off writeOff accountOperations amounts
     */
    protected BigDecimal writeOffAmount;

    /**
     * last payment Date
     */
    protected Date paymentDate;

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
    public InvoiceStatusEnum getRealTimeStatus() {
        return realTimeStatus;
    }

    /**
     * @param realTimeStatus the realTimeStatus to set
     */
    public void setRealTimeStatus(InvoiceStatusEnum realTimeStatus) {
        this.realTimeStatus = realTimeStatus;
    }

    public Date getIntialCollectionDate() {
        return intialCollectionDate;
    }

    public void setIntialCollectionDate(Date intialCollectionDate) {
        this.intialCollectionDate = intialCollectionDate;
    }
}