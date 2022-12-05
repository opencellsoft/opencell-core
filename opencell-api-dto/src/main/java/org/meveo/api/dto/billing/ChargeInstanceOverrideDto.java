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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * The Class ChargeInstanceOverrideDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ChargeInstanceOverrideDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5653231200327069483L;

    /** The charge instance code. */
    @XmlAttribute(required = true)
    private String chargeInstanceCode;

    /** The amount without tax. */
    @XmlElement(required = false)
    private BigDecimal amountWithoutTax;

    /** The amount with tax. */
    @XmlElement(required = false)
    private BigDecimal amountWithTax;

    /** The description. */
    private String description;

    /**
     * Gets the charge instance code.
     *
     * @return the charge instance code
     */
    public String getChargeInstanceCode() {
        return chargeInstanceCode;
    }

    /**
     * Sets the charge instance code.
     *
     * @param chargeInstanceCode the new charge instance code
     */
    public void setChargeInstanceCode(String chargeInstanceCode) {
        this.chargeInstanceCode = chargeInstanceCode;
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

    @Override
    public String toString() {
        return "ChargeInstanceOverrideDto [chargeInstanceCode=" + chargeInstanceCode + ", amountWithoutTax=" + amountWithoutTax + ", amountWithTax=" + amountWithTax
                + ", description" + description + "]";
    }
}