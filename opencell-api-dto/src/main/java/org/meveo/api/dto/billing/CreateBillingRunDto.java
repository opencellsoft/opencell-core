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

/**
 * 
 */
package org.meveo.api.dto.billing;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.billing.BillingProcessTypesEnum;
import org.meveo.model.billing.BillingRunAutomaticActionEnum;
import org.meveo.model.billing.ReferenceDateEnum;

/**
 * The Class CreateBillingRunDto.
 *
 * @author anasseh
 * @author akadid abdelmounaim
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */

@XmlRootElement(name = "CreateBillingRunDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class CreateBillingRunDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The billing cycle code. */
    @XmlAttribute(required = true)
    private String billingCycleCode;

    /** The billing run type enum. */
    @XmlAttribute(required = true)
    private BillingProcessTypesEnum billingRunTypeEnum;

    /** The start date. */
    private Date startDate;

    /** The end date. */
    private Date endDate;

    /** The invoice date. */
    private Date invoiceDate;

    /** The last transaction date. */
    private Date lastTransactionDate;

    /** The reference date. */
    private ReferenceDateEnum referenceDate;
    
    /** Custom fields. */
    private CustomFieldsDto customFields;

    /**
     * Collection Date used for payment collection date.
     */
    private Date collectionDate;

    /**
     * To decide whether or not dates should be recomputed at invoice validation.
     */
    private Boolean computeDatesAtValidation;

    private Boolean skipValidationScript = false;
    
    private BillingRunAutomaticActionEnum rejectAutoAction;
    
    private BillingRunAutomaticActionEnum suspectAutoAction; 

    public Boolean getSkipValidationScript() {
		return skipValidationScript;
	}

	public void setSkipValidationScript(Boolean skipValidationScript) {
		this.skipValidationScript = skipValidationScript;
	}

	public BillingRunAutomaticActionEnum getRejectAutoAction() {
		return rejectAutoAction;
	}

	public void setRejectAutoAction(BillingRunAutomaticActionEnum rejectAutoAction) {
		this.rejectAutoAction = rejectAutoAction;
	}

	public BillingRunAutomaticActionEnum getSuspectAutoAction() {
		return suspectAutoAction;
	}

	public void setSuspectAutoAction(BillingRunAutomaticActionEnum suspectAutoAction) {
		this.suspectAutoAction = suspectAutoAction;
	}

	/**
     * Instantiates a new creates the billing run dto.
     */
    public CreateBillingRunDto() {

    }

    /**
     * Gets the billing cycle code.
     *
     * @return the billingCycleCode
     */
    public String getBillingCycleCode() {
        return billingCycleCode;
    }

    /**
     * Sets the billing cycle code.
     *
     * @param billingCycleCode the billingCycleCode to set
     */
    public void setBillingCycleCode(String billingCycleCode) {
        this.billingCycleCode = billingCycleCode;
    }

    /**
     * Gets the billing run type enum.
     *
     * @return the billingRunTypeEnum
     */
    public BillingProcessTypesEnum getBillingRunTypeEnum() {
        return billingRunTypeEnum;
    }

    /**
     * Sets the billing run type enum.
     *
     * @param billingRunTypeEnum the billingRunTypeEnum to set
     */
    public void setBillingRunTypeEnum(BillingProcessTypesEnum billingRunTypeEnum) {
        this.billingRunTypeEnum = billingRunTypeEnum;
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

    @Override
    public String toString() {
        return "CreateBillingRunDto{" +
                "billingCycleCode='" + billingCycleCode + '\'' +
                ", billingRunTypeEnum=" + billingRunTypeEnum +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", invoiceDate=" + invoiceDate +
                ", lastTransactionDate=" + lastTransactionDate +
                ", referenceDate=" + referenceDate +
                ", customFields=" + customFields +
                '}';
    }
}