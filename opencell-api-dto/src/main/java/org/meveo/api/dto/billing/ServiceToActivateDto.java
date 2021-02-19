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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.catalog.ServiceTemplate;

/**
 * The Class ServiceToActivateDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "ServiceToActivate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceToActivateDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3815026205495621916L;

    /** The code. */
    @XmlAttribute(required = true)
    private String code;

    /** The Service Instance code. */
    @XmlAttribute
    private String overrideCode;

    /** The description. */
    @XmlAttribute
    private String description;

    /** The quantity. */
    @XmlElement(required = false)
    private BigDecimal quantity;

    /** The subscription date. */
    private Date subscriptionDate;

    /** The charge instance overrides. */
    private ChargeInstanceOverridesDto chargeInstanceOverrides;

    /** The custom fields. */
    private CustomFieldsDto customFields;

    /** The service template. */
    @XmlTransient
    // @ApiModelProperty(hidden = true)
    private ServiceTemplate serviceTemplate;

    /** The rate until date. */
    private Date rateUntilDate;
    
    /** The amount PS. */
    private BigDecimal amountPS;
    
    /** The calendar PS code. */
    private String calendarPSCode;
    
    /** The payment day in month PS. */
    private Integer paymentDayInMonthPS;

    /**
     * Expression to determine minimum amount value
     */
    private String minimumAmountEl;

    /**
     * Expression to determine rated transaction description to reach minimum amount value
     */
    private String minimumLabelEl;

    /**
     * Corresponding to minimum invoice subcategory
     */
    private String minimumInvoiceSubCategory;

    /**
     * Corresponding to minimum one shot charge template code.
     */
    private String minimumChargeTemplate;
    
    /**
     *  The subscribed till date. 
     */
    private Date subscribedTillDate;
    
    /**
     * The renewal service.
     */
    private SubscriptionRenewalDto serviceRenewal;

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
     * Gets the subscription date.
     *
     * @return the subscription date
     */
    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    /**
     * Sets the subscription date.
     *
     * @param subscriptionDate the new subscription date
     */
    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    /**
     * Gets the service template.
     *
     * @return the service template
     */
    public ServiceTemplate getServiceTemplate() {
        return serviceTemplate;
    }

    /**
     * Sets the service template.
     *
     * @param serviceTemplate the new service template
     */
    public void setServiceTemplate(ServiceTemplate serviceTemplate) {
        this.serviceTemplate = serviceTemplate;
    }

    /**
     * Gets the charge instance overrides.
     *
     * @return the charge instance overrides
     */
    public ChargeInstanceOverridesDto getChargeInstanceOverrides() {
        return chargeInstanceOverrides;
    }

    /**
     * Sets the charge instance overrides.
     *
     * @param chargeInstanceOverrides the new charge instance overrides
     */
    public void setChargeInstanceOverrides(ChargeInstanceOverridesDto chargeInstanceOverrides) {
        this.chargeInstanceOverrides = chargeInstanceOverrides;
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
     * Gets the rate until date.
     *
     * @return the rate until date
     */
    public Date getRateUntilDate() {
        return rateUntilDate;
    }

    /**
     * Sets the rate until date.
     *
     * @param rateUtilDate the new rate until date
     */
    public void setRateUntilDate(Date rateUtilDate) {
        this.rateUntilDate = rateUtilDate;
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
     * Gets the amount PS.
     *
     * @return the amountPS
     */
    public BigDecimal getAmountPS() {
        return amountPS;
    }

    /**
     * Sets the amount PS.
     *
     * @param amountPS the amountPS to set
     */
    public void setAmountPS(BigDecimal amountPS) {
        this.amountPS = amountPS;
    }

    /**
     * Gets the calendar PS code.
     *
     * @return the calendarPSCode
     */
    public String getCalendarPSCode() {
        return calendarPSCode;
    }

    /**
     * Sets the calendar PS code.
     *
     * @param calendarPSCode the calendarPSCode to set
     */
    public void setCalendarPSCode(String calendarPSCode) {
        this.calendarPSCode = calendarPSCode;
    }
    
    /**
     * Gets the payment day in month PS.
     *
     * @return the payment day in month PS
     */
    public Integer getPaymentDayInMonthPS() {
        return paymentDayInMonthPS;
    }


    /**
     * Sets the payment day in month PS.
     *
     * @param paymentDayInMonthPS the new payment day in month PS
     */
    public void setPaymentDayInMonthPS(Integer paymentDayInMonthPS) {
        this.paymentDayInMonthPS = paymentDayInMonthPS;
    }

    /**
     * Gets MinimumAmountEl expression
     * @return MinimumAmountEl expression
     */
    public String getMinimumAmountEl() {
        return minimumAmountEl;
    }

    /**
     * Sets minimumAmountEl expression
     * @param minimumAmountEl
     */
    public void setMinimumAmountEl(String minimumAmountEl) {
        this.minimumAmountEl = minimumAmountEl;
    }

    /**
     * Gets MinimumLabelEl expression
     * @return MinimumLabelEl expression
     */
    public String getMinimumLabelEl() {
        return minimumLabelEl;
    }

    /**
     * Sets MinimumLabelEl expression
     * @param minimumLabelEl
     */
    public void setMinimumLabelEl(String minimumLabelEl) {
        this.minimumLabelEl = minimumLabelEl;
    }

    /**
     * Gets the Minimum InvoiceSubCategory code
     * @return the InvoiceSubCategory
     */
    public String getMinimumInvoiceSubCategory() {
        return minimumInvoiceSubCategory;
    }

    /**
     * Sets the Minimum InvoiceSubCategory
     *
     * @param minimumInvoiceSubCategory
     */
    public void setMinimumInvoiceSubCategory(String minimumInvoiceSubCategory) {
        this.minimumInvoiceSubCategory = minimumInvoiceSubCategory;
    }

    public String getMinimumChargeTemplate() {
        return minimumChargeTemplate;
    }

    public void setMinimumChargeTemplate(String minimumChargeTemplate) {
        this.minimumChargeTemplate = minimumChargeTemplate;
    }
    
    /**
     * Gets the subscribed till date.
     *
     * @return the subscribedTillDate
     */
    public Date getSubscribedTillDate() {
        return subscribedTillDate;
    }

    /**
     * Sets the subscribed till date.
     *
     * @param subscribedTillDate the new subscribedTillDate
     */
    public void setSubscribedTillDate(Date subscribedTillDate) {
        this.subscribedTillDate = subscribedTillDate;
    }
    
    /**
     * Gets the service renewal
     *
     * @return the service renewal
     */
    public SubscriptionRenewalDto getServiceRenewal() {
        return serviceRenewal;
    }

    /**
     * Sets the service renewal.
     *
     * @param serviceRenewal the new service renewal
     */
    public void setServiceRenewal(SubscriptionRenewalDto serviceRenewal) {
        this.serviceRenewal = serviceRenewal;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("ServiceToActivateDto [code=%s, quantity=%s, subscriptionDate=%s, chargeInstanceOverrides=%s, customFields=%s]", code, quantity, subscriptionDate,
                chargeInstanceOverrides, customFields);
    }
}