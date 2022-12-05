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

package org.meveo.api.dto;

import java.math.BigDecimal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.TaxInvoiceAgregate;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Invoice category invoice aggregate DTO
 */
@XmlRootElement(name = "CategoryInvoiceAgregate")
@XmlAccessorType(XmlAccessType.FIELD)
public class TaxInvoiceAggregateDto extends BaseEntityDto {

    private static final long serialVersionUID = 6165612614574594919L;

    /** The description */
    @Schema(description = "The description")
    private String description;

    /** The item number */
    @Schema(description = "The item number")
    private Integer itemNumber;

    /** The amount without tax */
    @Schema(description = "The amount without tax")
    private BigDecimal amountWithoutTax;

    /** The amount tax */
    @Schema(description = "The amount tax")
    private BigDecimal amountTax;

    /** The amount with tax */
    @Schema(description = "The amount with tax")
    private BigDecimal amountWithTax;

    /** The taxes code */
    @Schema(description = "The taxes code")
    private String taxCode;

    /** The tax percent applied */
    @Schema(description = "The tax percent applied")
    private BigDecimal taxPercent;

    /** The accounting code. */
    @Schema(description = "The accounting code")
    private String accountingCode;

    /**
     * Instantiates a new tax invoice aggregate dto.
     * 
     * @param taxAggregate Tax aggregate entity
     */
    public TaxInvoiceAggregateDto(TaxInvoiceAgregate taxAggregate) {

        this.description = taxAggregate.getDescription();
        this.amountWithoutTax = taxAggregate.getAmountWithoutTax();
        this.amountWithTax = taxAggregate.getAmountWithTax();
        this.amountTax = taxAggregate.getAmountTax();
        if (taxAggregate.getTax() != null) {
            this.taxCode = taxAggregate.getTax().getCode();
        }
        this.taxPercent = taxAggregate.getTaxPercent();
        if (taxAggregate.getAccountingCode() != null) {
            this.accountingCode = taxAggregate.getAccountingCode().getCode();
        }
    }

    /**
     * Instantiates a new tax invoice aggregate dto.
     */
    public TaxInvoiceAggregateDto() {
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

    /**
     * Gets the item number.
     *
     * @return the item number
     */
    public Integer getItemNumber() {
        return itemNumber;
    }

    /**
     * Sets the item number.
     *
     * @param itemNumber the new item number
     */
    public void setItemNumber(Integer itemNumber) {
        this.itemNumber = itemNumber;
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
     * Gets the tax percent applied
     *
     * @return The tax percent applied
     */
    public BigDecimal getTaxPercent() {
        return taxPercent;
    }

    /**
     * Sets the tax percent applied
     *
     * @param taxPercent The tax percent applied
     */
    public void setTaxPercent(BigDecimal taxPercent) {
        this.taxPercent = taxPercent;
    }

    /**
     * Gets the code of a tax applied
     *
     * @return the taxes codes
     */
    public String getTaxCode() {
        return taxCode;
    }

    /**
     * Sets the code of a tax applied
     *
     * @param taxCode Code of a tax applied
     */
    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    /**
     * Gets the accounting code.
     *
     * @return the accounting code
     */
    public String getAccountingCode() {
        return accountingCode;
    }

    /**
     * Sets the accounting code.
     *
     * @param accountingCode the new accounting code
     */
    public void setAccountingCode(String accountingCode) {
        this.accountingCode = accountingCode;
    }
}