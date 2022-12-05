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

package org.meveo.api.dto.catalog;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The Class OneShotChargeTemplateWithPriceDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "OneShotChargeTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class OneShotChargeTemplateWithPriceDto {

    /** The charge code. */
    private String chargeCode;

    /** The description. */
    private String description;

    /** The unit price without tax. */
    private Double unitPriceWithoutTax;

    /** The tax percent. */
    private Double taxPercent;

    /** The tax code. */
    private String taxCode;

    /** The tax description. */
    private String taxDescription;

    /**
     * Instantiates a new one shot charge template with price dto.
     */
    public OneShotChargeTemplateWithPriceDto() {

    }

    /**
     * Gets the charge code.
     *
     * @return the charge code
     */
    public String getChargeCode() {
        return chargeCode;
    }

    /**
     * Sets the charge code.
     *
     * @param chargeCode the new charge code
     */
    public void setChargeCode(String chargeCode) {
        this.chargeCode = chargeCode;
    }

    /**
     * Gets the unit price without tax.
     *
     * @return the unit price without tax
     */
    public Double getUnitPriceWithoutTax() {
        return unitPriceWithoutTax;
    }

    /**
     * Sets the unit price without tax.
     *
     * @param unitPriceWithoutTax the new unit price without tax
     */
    public void setUnitPriceWithoutTax(Double unitPriceWithoutTax) {
        this.unitPriceWithoutTax = unitPriceWithoutTax;
    }

    /**
     * Gets the tax percent.
     *
     * @return the tax percent
     */
    public Double getTaxPercent() {
        return taxPercent;
    }

    /**
     * Sets the tax percent.
     *
     * @param taxPercent the new tax percent
     */
    public void setTaxPercent(Double taxPercent) {
        this.taxPercent = taxPercent;
    }

    /**
     * Gets the tax code.
     *
     * @return the tax code
     */
    public String getTaxCode() {
        return taxCode;
    }

    /**
     * Sets the tax code.
     *
     * @param taxCode the new tax code
     */
    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    /**
     * Gets the tax description.
     *
     * @return the tax description
     */
    public String getTaxDescription() {
        return taxDescription;
    }

    /**
     * Sets the tax description.
     *
     * @param taxDescription the new tax description
     */
    public void setTaxDescription(String taxDescription) {
        this.taxDescription = taxDescription;
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
        return "OneShotChargeTemplateWithPriceDto [chargeCode=" + chargeCode + ", description=" + description + ", unitPriceWithoutTax=" + unitPriceWithoutTax + ", taxPercent="
                + taxPercent + ", taxCode=" + taxCode + ", taxDescription=" + taxDescription + "]";
    }
 
}