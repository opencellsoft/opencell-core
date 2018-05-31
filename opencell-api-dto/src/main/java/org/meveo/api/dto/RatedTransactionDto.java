/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.api.dto;

import java.math.BigDecimal;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.RatedTransaction;

/**
 * The Class RatedTransactionDto.
 *
 * @author R.AITYAAZZA
 * @author Said Ramli
 * @lastModifiedVersion 5.1
 */

@XmlRootElement(name = "RatedTransaction")
@XmlAccessorType(XmlAccessType.FIELD)
public class RatedTransactionDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7627662294414998797L;

    /** The usage date. */
    @XmlElement(required = true)
    private Date usageDate;

    /** The unit amount without tax. */
    private BigDecimal unitAmountWithoutTax;

    /** The unit amount with tax. */
    private BigDecimal unitAmountWithTax;

    /** The unit amount tax. */
    private BigDecimal unitAmountTax;

    /** The quantity. */
    private BigDecimal quantity;

    /** The amount without tax. */
    @XmlElement(required = true)
    private BigDecimal amountWithoutTax;

    /** The amount with tax. */
    @XmlElement(required = true)
    private BigDecimal amountWithTax;

    /** The amount tax. */
    @XmlElement(required = true)
    private BigDecimal amountTax;

    /** The code. */
    @XmlElement(required = true)
    private String code;

    /** The description. */
    private String description;

    /** The unity description. */
    private String unityDescription;

    /** The priceplan code. */
    private String priceplanCode;

    /** The do not trigger invoicing. */
    private boolean doNotTriggerInvoicing = false;
    
    /** The start date. */
    private Date startDate;

    /** The end date. */
    private Date endDate;


    /** parameter1 : used to set more onformations in case of "DETAILLED" invoice. */
    private String parameter1;

    /** parameter2 : used to set more onformations in case of "DETAILLED" invoice. */
    private String parameter2;

    /** parameter2 : used to set more onformations in case of "DETAILLED" invoice. */
    private String parameter3;

    /**
     * Instantiates a new rated transaction dto.
     */
    public RatedTransactionDto() {

    }

    /**
     * Instantiates a new rated transaction dto.
     *
     * @param ratedTransaction the rated transaction
     */
    public RatedTransactionDto(RatedTransaction ratedTransaction) {
        this.setUsageDate(ratedTransaction.getUsageDate());
        this.setUnitAmountWithoutTax(ratedTransaction.getUnitAmountWithoutTax());
        this.setUnitAmountWithTax(ratedTransaction.getUnitAmountWithTax());
        this.setUnitAmountTax(ratedTransaction.getUnitAmountWithTax());
        this.setQuantity(ratedTransaction.getQuantity());
        this.setAmountWithoutTax(ratedTransaction.getAmountWithoutTax());
        this.setAmountWithTax(ratedTransaction.getAmountWithTax());
        this.setAmountTax(ratedTransaction.getAmountWithTax());
        this.setCode(ratedTransaction.getCode());
        this.setDescription(ratedTransaction.getDescription());
        this.setUnityDescription(ratedTransaction.getUnityDescription());
        if (ratedTransaction.getPriceplan() != null) {
            this.setPriceplanCode(ratedTransaction.getPriceplan().getCode());
        }
        this.setDoNotTriggerInvoicing(ratedTransaction.isDoNotTriggerInvoicing());
        this.setStartDate(ratedTransaction.getStartDate());
        this.setEndDate(ratedTransaction.getEndDate());
    }

    /**
     * Gets the usage date.
     *
     * @return the usage date
     */
    public Date getUsageDate() {
        return usageDate;
    }

    /**
     * Sets the usage date.
     *
     * @param usageDate the new usage date
     */
    public void setUsageDate(Date usageDate) {
        this.usageDate = usageDate;
    }

    /**
     * Gets the unit amount without tax.
     *
     * @return the unit amount without tax
     */
    public BigDecimal getUnitAmountWithoutTax() {
        return unitAmountWithoutTax;
    }

    /**
     * Sets the unit amount without tax.
     *
     * @param unitAmountWithoutTax the new unit amount without tax
     */
    public void setUnitAmountWithoutTax(BigDecimal unitAmountWithoutTax) {
        this.unitAmountWithoutTax = unitAmountWithoutTax;
    }

    /**
     * Gets the unit amount with tax.
     *
     * @return the unit amount with tax
     */
    public BigDecimal getUnitAmountWithTax() {
        return unitAmountWithTax;
    }

    /**
     * Sets the unit amount with tax.
     *
     * @param unitAmountWithTax the new unit amount with tax
     */
    public void setUnitAmountWithTax(BigDecimal unitAmountWithTax) {
        this.unitAmountWithTax = unitAmountWithTax;
    }

    /**
     * Gets the unit amount tax.
     *
     * @return the unit amount tax
     */
    public BigDecimal getUnitAmountTax() {
        return unitAmountTax;
    }

    /**
     * Sets the unit amount tax.
     *
     * @param unitAmountTax the new unit amount tax
     */
    public void setUnitAmountTax(BigDecimal unitAmountTax) {
        this.unitAmountTax = unitAmountTax;
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
     * Checks if is do not trigger invoicing.
     *
     * @return true, if is do not trigger invoicing
     */
    public boolean isDoNotTriggerInvoicing() {
        return doNotTriggerInvoicing;
    }

    /**
     * Sets the do not trigger invoicing.
     *
     * @param doNotTriggerInvoicing the new do not trigger invoicing
     */
    public void setDoNotTriggerInvoicing(boolean doNotTriggerInvoicing) {
        this.doNotTriggerInvoicing = doNotTriggerInvoicing;
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
     * Gets the unity description.
     *
     * @return the unity description
     */
    public String getUnityDescription() {
        return unityDescription;
    }

    /**
     * Sets the unity description.
     *
     * @param unityDescription the new unity description
     */
    public void setUnityDescription(String unityDescription) {
        this.unityDescription = unityDescription;
    }

    /**
     * Gets the priceplan code.
     *
     * @return the priceplan code
     */
    public String getPriceplanCode() {
        return priceplanCode;
    }

    /**
     * Sets the priceplan code.
     *
     * @param priceplanCode the new priceplan code
     */
    public void setPriceplanCode(String priceplanCode) {
        this.priceplanCode = priceplanCode;
    }

    /**
     * Gets the start date.
     *
     * @return the start date
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date.
     *
     * @param startDate the start date
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the end date.
     *
     * @return the end date
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date.
     *
     * @param endDate the end date
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    
    /**
     * Gets the parameter 1.
     *
     * @return the parameter 1
     */
    public String getParameter1() {
        return parameter1;
    }

    /**
     * Sets the parameter 1.
     *
     * @param parameter1 the new parameter 1
     */
    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    /**
     * Gets the parameter 2.
     *
     * @return the parameter 2
     */
    public String getParameter2() {
        return parameter2;
    }

    /**
     * Sets the parameter 2.
     *
     * @param parameter2 the new parameter 2
     */
    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    /**
     * Gets the parameter 3.
     *
     * @return the parameter 3
     */
    public String getParameter3() {
        return parameter3;
    }

    /**
     * Sets the parameter 3.
     *
     * @param parameter3 the new parameter 3
     */
    public void setParameter3(String parameter3) {
        this.parameter3 = parameter3;
    }

}
