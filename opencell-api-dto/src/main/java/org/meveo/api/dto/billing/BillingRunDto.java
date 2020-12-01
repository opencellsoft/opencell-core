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

package org.meveo.api.dto.billing;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.AuditableEntityDto;
import org.meveo.api.dto.BillingCycleDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.billing.BillingProcessTypesEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.ReferenceDateEnum;

import io.swagger.v3.oas.annotations.Hidden;

/**
 * The Class BillingRunDto.
 * 
 * @author akadid abdelmounaim
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@XmlRootElement(name = "BillingRun")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillingRunDto extends AuditableEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The process date. */
    private Date processDate;

    /** The status. */
    private BillingRunStatusEnum status;

    /** The status date. */
    private Date statusDate;

    /** The billing cycle. */
    private BillingCycleDto billingCycle;

    /** The billing account number. */
    private Integer billingAccountNumber;

    /** The billable billing acount number. */
    private Integer billableBillingAcountNumber;

    /** The producible invoice number. */
    private Integer producibleInvoiceNumber;

    /** The producible amount without tax. */
    private BigDecimal producibleAmountWithoutTax;

    /**
     * The producible amount tax.
     */
    private BigDecimal producibleAmountTax;

    /**
     * The Invoice number.
     */
    private Integer InvoiceNumber;

    /**
     * The producible amount with tax.
     */
    private BigDecimal producibleAmountWithTax;

    /**
     * The pr amount without tax.
     */
    private BigDecimal prAmountWithoutTax;

    /**
     * The pr amount with tax.
     */
    private BigDecimal prAmountWithTax;

    /**
     * The pr amount tax.
     */
    private BigDecimal prAmountTax;

    /**
     * The process type.
     */
    private BillingProcessTypesEnum processType;

    /**
     * The start date.
     */
    private Date startDate;

    /**
     * The end date.
     */
    private Date endDate;

    /**
     * The invoice date.
     */
    private Date invoiceDate;

    /**
     * Include in invoice Rated transactions up to that date
     */
    private Date lastTransactionDate;

    /**
     * The reference date.
     */
    private ReferenceDateEnum referenceDate;

    /**
     * The rejection reason.
     */
    private String rejectionReason;

    /**
     * The currency code.
     */
    private String currencyCode;

    /**
     * The country code.
     */
    private String countryCode;

    /**
     * The language code.
     */
    private String languageCode;

    /**
     * The selected billing accounts.
     */
    private String selectedBillingAccounts;

    /**
     * Custom fields.
     */
    private CustomFieldsDto customFields;

    /**
     * EL to compute invoice.initialCollectionDate delay.
     */
    private Date collectionDate;

    /**
     * To decide whether or not dates should be recomputed at invoice validation.
     */
    private Boolean computeDatesAtValidation;

    /**
     * Instantiates a new billing run dto.
     */
    public BillingRunDto() {

    }

    /**
     * Gets the process date.
     *
     * @return the processDate
     */
    public Date getProcessDate() {
        return processDate;
    }

    /**
     * Sets the process date.
     *
     * @param processDate the processDate to set
     */
    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public BillingRunStatusEnum getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the status to set
     */
    public void setStatus(BillingRunStatusEnum status) {
        this.status = status;
    }

    /**
     * Gets the status date.
     *
     * @return the statusDate
     */
    public Date getStatusDate() {
        return statusDate;
    }

    /**
     * Sets the status date.
     *
     * @param statusDate the statusDate to set
     */
    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    /**
     * Gets the billing cycle.
     *
     * @return the billingCycle
     */
    public BillingCycleDto getBillingCycle() {
        return billingCycle;
    }

    /**
     * Sets the billing cycle.
     *
     * @param billingCycle the billingCycle to set
     */
    public void setBillingCycle(BillingCycleDto billingCycle) {
        this.billingCycle = billingCycle;
    }

    /**
     * Gets the billing account number.
     *
     * @return the billingAccountNumber
     */
    public Integer getBillingAccountNumber() {
        return billingAccountNumber;
    }

    /**
     * Sets the billing account number.
     *
     * @param billingAccountNumber the billingAccountNumber to set
     */
    public void setBillingAccountNumber(Integer billingAccountNumber) {
        this.billingAccountNumber = billingAccountNumber;
    }

    /**
     * Gets the billable billing acount number.
     *
     * @return the billableBillingAcountNumber
     */
    public Integer getBillableBillingAcountNumber() {
        return billableBillingAcountNumber;
    }

    /**
     * Sets the billable billing acount number.
     *
     * @param billableBillingAcountNumber the billableBillingAcountNumber to set
     */
    public void setBillableBillingAcountNumber(Integer billableBillingAcountNumber) {
        this.billableBillingAcountNumber = billableBillingAcountNumber;
    }

    /**
     * Gets the producible invoice number.
     *
     * @return the producibleInvoiceNumber
     */
    public Integer getProducibleInvoiceNumber() {
        return producibleInvoiceNumber;
    }

    /**
     * Sets the producible invoice number.
     *
     * @param producibleInvoiceNumber the producibleInvoiceNumber to set
     */
    public void setProducibleInvoiceNumber(Integer producibleInvoiceNumber) {
        this.producibleInvoiceNumber = producibleInvoiceNumber;
    }

    /**
     * Gets the producible amount without tax.
     *
     * @return the producibleAmountWithoutTax
     */
    public BigDecimal getProducibleAmountWithoutTax() {
        return producibleAmountWithoutTax;
    }

    /**
     * Sets the producible amount without tax.
     *
     * @param producibleAmountWithoutTax the producibleAmountWithoutTax to set
     */
    public void setProducibleAmountWithoutTax(BigDecimal producibleAmountWithoutTax) {
        this.producibleAmountWithoutTax = producibleAmountWithoutTax;
    }

    /**
     * Gets the producible amount tax.
     *
     * @return the producibleAmountTax
     */
    public BigDecimal getProducibleAmountTax() {
        return producibleAmountTax;
    }

    /**
     * Sets the producible amount tax.
     *
     * @param producibleAmountTax the producibleAmountTax to set
     */
    public void setProducibleAmountTax(BigDecimal producibleAmountTax) {
        this.producibleAmountTax = producibleAmountTax;
    }

    /**
     * Gets the invoice number.
     *
     * @return the invoiceNumber
     */
    public Integer getInvoiceNumber() {
        return InvoiceNumber;
    }

    /**
     * Sets the invoice number.
     *
     * @param invoiceNumber the invoiceNumber to set
     */
    public void setInvoiceNumber(Integer invoiceNumber) {
        InvoiceNumber = invoiceNumber;
    }

    /**
     * Gets the producible amount with tax.
     *
     * @return the producibleAmountWithTax
     */
    public BigDecimal getProducibleAmountWithTax() {
        return producibleAmountWithTax;
    }

    /**
     * Sets the producible amount with tax.
     *
     * @param producibleAmountWithTax the producibleAmountWithTax to set
     */
    public void setProducibleAmountWithTax(BigDecimal producibleAmountWithTax) {
        this.producibleAmountWithTax = producibleAmountWithTax;
    }

    /**
     * Gets the pr amount without tax.
     *
     * @return the prAmountWithoutTax
     */
    public BigDecimal getPrAmountWithoutTax() {
        return prAmountWithoutTax;
    }

    /**
     * Sets the pr amount without tax.
     *
     * @param prAmountWithoutTax the prAmountWithoutTax to set
     */
    public void setPrAmountWithoutTax(BigDecimal prAmountWithoutTax) {
        this.prAmountWithoutTax = prAmountWithoutTax;
    }

    /**
     * Gets the pr amount with tax.
     *
     * @return the prAmountWithTax
     */
    public BigDecimal getPrAmountWithTax() {
        return prAmountWithTax;
    }

    /**
     * Sets the pr amount with tax.
     *
     * @param prAmountWithTax the prAmountWithTax to set
     */
    public void setPrAmountWithTax(BigDecimal prAmountWithTax) {
        this.prAmountWithTax = prAmountWithTax;
    }

    /**
     * Gets the pr amount tax.
     *
     * @return the prAmountTax
     */
    public BigDecimal getPrAmountTax() {
        return prAmountTax;
    }

    /**
     * Sets the pr amount tax.
     *
     * @param prAmountTax the prAmountTax to set
     */
    public void setPrAmountTax(BigDecimal prAmountTax) {
        this.prAmountTax = prAmountTax;
    }

    /**
     * Gets the process type.
     *
     * @return the processType
     */
    public BillingProcessTypesEnum getProcessType() {
        return processType;
    }

    /**
     * Sets the process type.
     *
     * @param processType the processType to set
     */
    public void setProcessType(BillingProcessTypesEnum processType) {
        this.processType = processType;
    }

    /**
     * Gets the start date.
     *
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date.
     *
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the end date.
     *
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date.
     *
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Gets the invoice date.
     *
     * @return the invoiceDate
     */
    public Date getInvoiceDate() {
        return invoiceDate;
    }

    /**
     * Sets the invoice date.
     *
     * @param invoiceDate the invoiceDate to set
     */
    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    /**
     * @return Include in invoice Rated transactions up to that date
     */
    public Date getLastTransactionDate() {
        return lastTransactionDate;
    }

    /**
     * Sets the last transaction date.
     *
     * @param lastTransactionDate Include in invoice Rated transactions up to that date
     */
    public void setLastTransactionDate(Date lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }

    /**
     * Gets the referenceDate
     *
     * @return the referenceDate
     */
    public ReferenceDateEnum getReferenceDate() {
        return referenceDate;
    }

    /**
     * Sets the referenceDate.
     *
     * @param referenceDate the new referenceDate
     */
    public void setReferenceDate(ReferenceDateEnum referenceDate) {
        this.referenceDate = referenceDate;
    }

    /**
     * Gets the rejection reason.
     *
     * @return the rejectionReason
     */
    public String getRejectionReason() {
        return rejectionReason;
    }

    /**
     * Sets the rejection reason.
     *
     * @param rejectionReason the rejectionReason to set
     */
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    /**
     * Gets the currency code.
     *
     * @return the currencyCode
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * Sets the currency code.
     *
     * @param currencyCode the currencyCode to set
     */
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    /**
     * Gets the country code.
     *
     * @return the countryCode
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Sets the country code.
     *
     * @param countryCode the countryCode to set
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * Gets the language code.
     *
     * @return the languageCode
     */
    public String getLanguageCode() {
        return languageCode;
    }

    /**
     * Sets the language code.
     *
     * @param languageCode the languageCode to set
     */
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    /**
     * Gets the selected billing accounts.
     *
     * @return the selectedBillingAccounts
     */
    public String getSelectedBillingAccounts() {
        return selectedBillingAccounts;
    }

    /**
     * Sets the selected billing accounts.
     *
     * @param selectedBillingAccounts the selectedBillingAccounts to set
     */
    public void setSelectedBillingAccounts(String selectedBillingAccounts) {
        this.selectedBillingAccounts = selectedBillingAccounts;
    }

    /**
     * Gets the custom fields.
     *
     * @return the custom fields
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * Sets the custom fields.
     *
     * @param customFields the new custom fields
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    /**
     * Gets Collection Date.
     *
     * @return ollection Date.
     */
    public Date getCollectionDate() {
        return collectionDate;
    }

    /**
     * Sets Collection Date.
     *
     * @param collectionDate
     */
    public void setCollectionDate(Date collectionDate) {
        this.collectionDate = collectionDate;
    }

    /**
     * Gets computeDatesAtValidation.
     *
     * @return
     */
    public Boolean isComputeDatesAtValidation() {
        return computeDatesAtValidation;
    }

    /**
     * Sets computeDatesAtValidation
     *
     * @param computeDatesAtValidation
     */
    public void setComputeDatesAtValidation(Boolean computeDatesAtValidation) {
        this.computeDatesAtValidation = computeDatesAtValidation;
    }

    /**
     * Sets the from entity.
     *
     * @param billingRunEntity the new from entity
     */
    @Hidden
    public void setFromEntity(BillingRun billingRunEntity) {
        setAuditable(billingRunEntity);
        setProcessDate(billingRunEntity.getProcessDate());
        setStatus(billingRunEntity.getStatus());
        setStatusDate(billingRunEntity.getStatusDate());
        setBillingCycle(new BillingCycleDto(billingRunEntity.getBillingCycle(), new CustomFieldsDto()));
        setBillingAccountNumber(billingRunEntity.getBillingAccountNumber());
        setBillableBillingAcountNumber(billingRunEntity.getBillableBillingAcountNumber());
        setProducibleInvoiceNumber(billingRunEntity.getProducibleInvoiceNumber());
        setProducibleAmountWithoutTax(billingRunEntity.getProducibleAmountWithoutTax());
        setProducibleAmountTax(billingRunEntity.getProducibleAmountTax());
        setInvoiceNumber(billingRunEntity.getInvoiceNumber());
        setProducibleAmountWithTax(billingRunEntity.getProducibleAmountWithTax());
        setPrAmountWithoutTax(billingRunEntity.getPrAmountWithoutTax());
        setPrAmountWithTax(billingRunEntity.getPrAmountWithTax());
        setPrAmountTax(billingRunEntity.getPrAmountTax());
        setProcessType(billingRunEntity.getProcessType());
        setStartDate(billingRunEntity.getStartDate());
        setEndDate(billingRunEntity.getEndDate());
        setInvoiceDate(billingRunEntity.getInvoiceDate());
        setLastTransactionDate(billingRunEntity.getLastTransactionDate());
        setReferenceDate(billingRunEntity.getReferenceDate());
        setRejectionReason(billingRunEntity.getRejectionReason());
        setCurrencyCode(billingRunEntity.getCurrency() == null ? null : billingRunEntity.getCurrency().getCurrencyCode());
        setCountryCode(billingRunEntity.getCountry() == null ? null : billingRunEntity.getCountry().getCountryCode());
        setLanguageCode(billingRunEntity.getLanguage() == null ? null : billingRunEntity.getLanguage().getLanguageCode());
        setSelectedBillingAccounts(billingRunEntity.getSelectedBillingAccounts());
        setCollectionDate(billingRunEntity.getCollectionDate());
        setComputeDatesAtValidation(billingRunEntity.isComputeDatesAtValidation() == null ? null : billingRunEntity.isComputeDatesAtValidation());
    }

    @Override
    public String toString() {
        return "BillingRunDto{" +
                "processDate=" + processDate +
                ", status=" + status +
                ", statusDate=" + statusDate +
                ", billingCycle=" + billingCycle +
                ", billingAccountNumber=" + billingAccountNumber +
                ", billableBillingAcountNumber=" + billableBillingAcountNumber +
                ", producibleInvoiceNumber=" + producibleInvoiceNumber +
                ", producibleAmountWithoutTax=" + producibleAmountWithoutTax +
                ", producibleAmountTax=" + producibleAmountTax +
                ", InvoiceNumber=" + InvoiceNumber +
                ", producibleAmountWithTax=" + producibleAmountWithTax +
                ", prAmountWithoutTax=" + prAmountWithoutTax +
                ", prAmountWithTax=" + prAmountWithTax +
                ", prAmountTax=" + prAmountTax +
                ", processType=" + processType +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", invoiceDate=" + invoiceDate +
                ", lastTransactionDate=" + lastTransactionDate +
                ", referenceDate=" + referenceDate +
                ", rejectionReason='" + rejectionReason + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", languageCode='" + languageCode + '\'' +
                ", selectedBillingAccounts='" + selectedBillingAccounts + '\'' +
                ", customFields=" + customFields +
                '}';
    }
}