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

package org.meveo.api.dto.usage;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;

/**
 * The Class UsageDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "Usage")
@XmlAccessorType(XmlAccessType.FIELD)
public class UsageDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The date event. */
    private Date dateEvent;
    
    /** The code. */
    private String code;
    
    /** The description. */
    private String description;
    
    /** The unity description. */
    private String unityDescription;
    
    /** The unit amount without tax. */
    private BigDecimal unitAmountWithoutTax;
    
    /** The quantity. */
    private BigDecimal quantity;
    
    /** The amount without tax. */
    private BigDecimal amountWithoutTax;
    
    /** The parameter 1. */
    private String parameter1;
    
    /** The parameter 2. */
    private String parameter2;
    
    /** The parameter 3. */
    private String parameter3;
    
    /** The parameter extra. */
    private String parameterExtra;
    
    /** The offer code. */
    private String offerCode;
    
    /** The priceplan code. */
    private String priceplanCode;

    /**
     * Instantiates a new usage dto.
     */
    public UsageDto() {

    }

    /**
     * Gets the date event.
     *
     * @return the dateEvent
     */
    public Date getDateEvent() {
        return dateEvent;
    }

    /**
     * Sets the date event.
     *
     * @param dateEvent the dateEvent to set
     */
    public void setDateEvent(Date dateEvent) {
        this.dateEvent = dateEvent;
    }

    /**
     * Gets the unity description.
     *
     * @return the unityDescription
     */
    public String getUnityDescription() {
        return unityDescription;
    }

    /**
     * Sets the unity description.
     *
     * @param unityDescription the unityDescription to set
     */
    public void setUnityDescription(String unityDescription) {
        this.unityDescription = unityDescription;
    }

    /**
     * Gets the unit amount without tax.
     *
     * @return the unitAmountWithoutTax
     */
    public BigDecimal getUnitAmountWithoutTax() {
        return unitAmountWithoutTax;
    }

    /**
     * Sets the unit amount without tax.
     *
     * @param unitAmountWithoutTax the unitAmountWithoutTax to set
     */
    public void setUnitAmountWithoutTax(BigDecimal unitAmountWithoutTax) {
        this.unitAmountWithoutTax = unitAmountWithoutTax;
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
     * @param quantity the quantity to set
     */
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the amount without tax.
     *
     * @return the amountWithoutTax
     */
    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    /**
     * Sets the amount without tax.
     *
     * @param amountWithoutTax the amountWithoutTax to set
     */
    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    /**
     * Gets the parameter 1.
     *
     * @return the parameter1
     */
    public String getParameter1() {
        return parameter1;
    }

    /**
     * Sets the parameter 1.
     *
     * @param parameter1 the parameter1 to set
     */
    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    /**
     * Gets the parameter 2.
     *
     * @return the parameter2
     */
    public String getParameter2() {
        return parameter2;
    }

    /**
     * Sets the parameter 2.
     *
     * @param parameter2 the parameter2 to set
     */
    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    /**
     * Gets the parameter 3.
     *
     * @return the parameter3
     */
    public String getParameter3() {
        return parameter3;
    }

    /**
     * Sets the parameter 3.
     *
     * @param parameter3 the parameter3 to set
     */
    public void setParameter3(String parameter3) {
        this.parameter3 = parameter3;
    }

    /**
     * Gets the parameter extra.
     *
     * @return the parameter extra
     */
    public String getParameterExtra() {
        return parameterExtra;
    }

    /**
     * Sets the parameter extra.
     *
     * @param parameterExtra the new parameter extra
     */
    public void setParameterExtra(String parameterExtra) {
        this.parameterExtra = parameterExtra;
    }

    /**
     * Gets the offer code.
     *
     * @return the offerCode
     */
    public String getOfferCode() {
        return offerCode;
    }

    /**
     * Sets the offer code.
     *
     * @param offerCode the offerCode to set
     */
    public void setOfferCode(String offerCode) {
        this.offerCode = offerCode;
    }

    /**
     * Gets the priceplan code.
     *
     * @return the priceplanCode
     */
    public String getPriceplanCode() {
        return priceplanCode;
    }

    /**
     * Sets the priceplan code.
     *
     * @param priceplanCode the priceplanCode to set
     */
    public void setPriceplanCode(String priceplanCode) {
        this.priceplanCode = priceplanCode;
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
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
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
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "UsageDto [dateEvent=" + dateEvent + ", code=" + code + ", description=" + description + ", unityDescription=" + unityDescription + ", unitAmountWithoutTax="
                + unitAmountWithoutTax + ", quantity=" + quantity + ", amountWithoutTax=" + amountWithoutTax + ", parameter1=" + parameter1 + ", parameter2=" + parameter2
                + ", parameter3=" + parameter3 + ", offerCode=" + offerCode + ", priceplanCode=" + priceplanCode + "]";
    }
}
