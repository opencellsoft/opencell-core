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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.IEntityDto;
import org.meveo.api.dto.catalog.DiscountPlanDto;

/**
 * The Class ServiceToUpdateDto.
 *
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@XmlRootElement(name = "ServiceToUpdate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceToUpdateDto implements Serializable, IEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3815026205495621916L;

    /** Service instance ID. */
    @XmlAttribute()
    private Long id;

    /**
     * Service instance code. Note: not a unique identifier as service can be activated mnultiple times
     */
    @XmlAttribute()
    private String code;

    /** The Service Instance code. */
    @XmlAttribute
    private String overrideCode;

    /** Description. */
    @XmlAttribute()
    private String description;

    /** Quantity. */
    @XmlElement(required = false)
    private BigDecimal quantity;

    /** Service suspension or reactivation date - used in service suspension or reactivation API only. */
    private Date actionDate;

    /** End agreement date. */
    private Date endAgreementDate;

    /** The termination date. */
    private Date terminationDate;

    /** The termination reason. */
    private String terminationReason;

    /** The renewal service. */
    private SubscriptionRenewalDto serviceRenewal;

    /** Custom fields. */
    private CustomFieldsDto customFields;

    private List<DiscountPlanDto> discountPlansForInstantiation;
    
    private List<String> discountPlanForTermination;
	
	private Date priceVersionDate;
	
    /**
     * Gets the id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     *
     * @param code the new code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the service instance code.
     *
     * @return the overrideCode
     */
    public String getOverrideCode() {
        return overrideCode;
    }

    /**
     * Sets the service instance code.
     *
     * @param overrideCode the service instance code.
     */
    public void setOverrideCode(String overrideCode) {
        this.overrideCode = overrideCode;
    }

    /**
     * Gets the action date.
     *
     * @return the action date
     */
    public Date getActionDate() {
        return actionDate;
    }

    /**
     * Sets the action date.
     *
     * @param actionDate the new action date
     */
    public void setActionDate(Date actionDate) {
        this.actionDate = actionDate;
    }

    /**
     * Gets the end agreement date.
     *
     * @return the end agreement date
     */
    public Date getEndAgreementDate() {
        return endAgreementDate;
    }

    /**
     * Sets the end agreement date.
     *
     * @param endAgreementDate the new end agreement date
     */
    public void setEndAgreementDate(Date endAgreementDate) {
        this.endAgreementDate = endAgreementDate;
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
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the quantity.
     *
     * @return the quantity
     */
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity.
     *
     * @param quantity the new quantity
     */
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the renewal service
     *
     * @return the renewal service
     */
    public SubscriptionRenewalDto getServiceRenewal() {
        return serviceRenewal;
    }

    /**
     * Sets the renewal service.
     *
     * @param serviceRenewal the new renewal service
     */
    public void setServiceRenewal(SubscriptionRenewalDto serviceRenewal) {
        this.serviceRenewal = serviceRenewal;
    }

    /**
     * Gets the termination date.
     *
     * @return the termination date
     */
    public Date getTerminationDate() {
        return terminationDate;
    }

    /**
     * Sets the termination date.
     *
     * @param terminationDate the new termination date
     */
    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    /**
     * Gets the termination reason.
     *
     * @return the termination reason
     */
    public String getTerminationReason() {
        return terminationReason;
    }

    /**
     * Sets the termination reason.
     *
     * @param terminationReason the new termination reason
     */
    public void setTerminationReason(String terminationReason) {
        this.terminationReason = terminationReason;
    }

    @Override
    public String toString() {
        return "ServiceToSuspendDto [code=" + code + ", actionDate=" + actionDate + "]";
    }

    public List<DiscountPlanDto> getDiscountPlansForInstantiation() {
        return discountPlansForInstantiation;
    }

    public List<String> getDiscountPlanForTermination() {
        return discountPlanForTermination;
    }

    public void setDiscountPlansForInstantiation(List<DiscountPlanDto> discountPlansForInstantiation) {
        this.discountPlansForInstantiation = discountPlansForInstantiation;
    }

    public void setDiscountPlanForTermination(List<String> discountPlanForTermination) {
        this.discountPlanForTermination = discountPlanForTermination;
    }
	
	public Date getPriceVersionDate() {
		return priceVersionDate;
	}
	
	public void setPriceVersionDate(Date priceVersionDate) {
		this.priceVersionDate = priceVersionDate;
	}
}