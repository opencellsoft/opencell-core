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

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.FilterDto;

/**
 * The Class GenerateInvoiceRequestDto.
 *
 * @author anasseh
 * @author Abdelmounaim Akadid
 * @author Said Ramli
 * @lastModifiedVersion 5.1
 */
@XmlRootElement(name = "GenerateInvoiceRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class GenerateInvoiceRequestDto {

    @XmlElement()
    private String targetType;

    @XmlElement()
    private String targetCode;

    @XmlElement()
    private String billingAccountCode;

    /**
     * The invoicing date.
     */
    @XmlElement(required = true)
    private Date invoicingDate;

    /**
     * The first transaction date.
     */
    @XmlElement()
    private Date firstTransactionDate;

    /**
     * The last transaction date.
     */
    @XmlElement()
    private Date lastTransactionDate;

    /**
     * The filter.
     */
    @XmlElement()
    private FilterDto filter;

    /**
     * The order number.
     */
    @XmlElement()
    private String orderNumber;

    /**
     * The generate XML.
     */
    private Boolean generateXML;

    /**
     * The generate PDF.
     */
    private Boolean generatePDF;

    /**
     * The generate AO.
     */
    private Boolean generateAO;

    /**
     * The custom fields.
     */
    private CustomFieldsDto customFields;

    /**
     * Includes rated transactions in the return value
     */
    private Boolean includeRatedTransactions;

    /**
     * apply mode for invoice minimum rules
     */
    private String applyMinimum;
    
    
    /**
     * if true then validation script is NOT executed
     */
    private Boolean skipValidation = false;

    /**
     * Instantiates a new generate invoice request dto.
     */
    public GenerateInvoiceRequestDto() {

    }

    /**
     * Gets the target type.
     *
     * @return the targetType
     */
    public String getTargetType() {
        return targetType;
    }

    /**
     * Sets the target type.
     *
     * @param targetType the targetType to set
     */
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    /**
     * Gets the target code.
     *
     * @return the targetCode
     */
    public String getTargetCode() {
        return targetCode;
    }

    /**
     * Sets the target code.
     *
     * @param targetCode the targetCode to set
     */
    public void setTargetCode(String targetCode) {
        this.targetCode = targetCode;
    }

    /**
     * Gets the invoicing date.
     *
     * @return the invoicingDate
     */
    public Date getInvoicingDate() {
        return invoicingDate;
    }

    /**
     * Sets the invoicing date.
     *
     * @param invoicingDate the invoicingDate to set
     */
    public void setInvoicingDate(Date invoicingDate) {
        this.invoicingDate = invoicingDate;
    }

    /**
     * Gets the last transaction date.
     *
     * @return the lastTransactionDate
     */
    public Date getLastTransactionDate() {
        return lastTransactionDate;
    }

    /**
     * Sets the last transaction date.
     *
     * @param lastTransactionDate the lastTransactionDate to set
     */
    public void setLastTransactionDate(Date lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }

    /**
     * Gets the filter.
     *
     * @return the filter
     */
    public FilterDto getFilter() {
        return filter;
    }

    /**
     * Sets the filter.
     *
     * @param filter the new filter
     */
    public void setFilter(FilterDto filter) {
        this.filter = filter;
    }

    /**
     * Gets the order number.
     *
     * @return the order number
     */
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * Sets the order number.
     *
     * @param orderNumber the new order number
     */
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * Gets the generate XML.
     *
     * @return the generateXML
     */
    public Boolean getGenerateXML() {
        return generateXML;
    }

    /**
     * Sets the generate XML.
     *
     * @param generateXML the generateXML to set
     */
    public void setGenerateXML(Boolean generateXML) {
        this.generateXML = generateXML;
    }

    /**
     * Gets the generate PDF.
     *
     * @return the generatePDF
     */
    public Boolean getGeneratePDF() {
        return generatePDF;
    }

    /**
     * Sets the generate PDF.
     *
     * @param generatePDF the generatePDF to set
     */
    public void setGeneratePDF(Boolean generatePDF) {
        this.generatePDF = generatePDF;
    }

    /**
     * Gets the generate AO.
     *
     * @return the generateAO
     */
    public Boolean getGenerateAO() {
        return generateAO;
    }

    /**
     * Sets the generate AO.
     *
     * @param generateAO the generateAO to set
     */
    public void setGenerateAO(Boolean generateAO) {
        this.generateAO = generateAO;
    }

    /**
     * Gets the first transaction date.
     *
     * @return the first transaction date
     */
    public Date getFirstTransactionDate() {
        return firstTransactionDate;
    }

    /**
     * Sets the first transaction date.
     *
     * @param firstTransactionDate the new first transaction date
     */
    public void setFirstTransactionDate(Date firstTransactionDate) {
        this.firstTransactionDate = firstTransactionDate;
    }

    /**
     * @return the customFields
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * @param customFields the customFields to set
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    /**
     * @return the billingAccountCode
     */
    public String getBillingAccountCode() {
        return billingAccountCode;
    }

    /**
     * @param billingAccountCode the billingAccountCode to set
     */
    public void setBillingAccountCode(String billingAccountCode) {
        this.billingAccountCode = billingAccountCode;
    }

    @Override
    public String toString() {
        return "GenerateInvoiceRequestDto [targetCode=" + targetCode + ", targetType=" + targetType + ", invoicingDate=" + invoicingDate + ", lastTransactionDate="
                + lastTransactionDate + ", filter=" + filter + ", orderNumber=" + orderNumber + ", generateXML=" + generateXML + ", generatePDF=" + generatePDF + ", generateAO="
                + generateAO + "]";
    }

    /**
     * Whether to include ratedTransactions in the return object or not
     *
     * @return boolean value
     */
    public Boolean getIncludeRatedTransactions() {
        return includeRatedTransactions;
    }

    /**
     * Whether to include ratedTransactions in the return object or not
     *
     * @param includeRatedTransactions boolean value
     */
    public void setIncludeRatedTransactions(Boolean includeRatedTransactions) {
        this.includeRatedTransactions = includeRatedTransactions;
    }

    /**
     * Whether to include ratedTransactions in the return object or not
     *
     * @return boolean value
     */
    public boolean isIncludeRatedTransactions() {
        return getIncludeRatedTransactions() != null ? getIncludeRatedTransactions() : false;
    }

    /**
     * Gets the applyMinimum
     *
     * @return the applyMinimum
     */
    public String getApplyMinimum() {
        return applyMinimum;
    }

    /**
     * Sets the applyMinimum
     *
     * @param applyMinimum the applyMinimum
     */
    public void setApplyMinimum(String applyMinimum) {
        this.applyMinimum = applyMinimum;
    }

	/**
	 * @return the skipValidation
	 */
	public Boolean getSkipValidation() {
		return skipValidation;
	}

	/**
	 * @param skipValidation the skipValidation to set
	 */
	public void setSkipValidation(Boolean skipValidation) {
		this.skipValidation = skipValidation;
	}
}