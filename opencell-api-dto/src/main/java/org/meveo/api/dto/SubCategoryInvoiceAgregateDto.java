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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class SubCategoryInvoiceAgregateDto.
 *
 * @author Edward P. Legaspi
 * @author R.AITYAAZZA
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "SubCategoryInvoiceAgregate")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubCategoryInvoiceAgregateDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6165612614574594919L;

    /** The item number. */
    private Integer itemNumber;

    /** The accounting code. */
    private String accountingCode;

    /** The description. */
    private String description;

    /** The quantity. Deprecated in v5.2 */
    @Deprecated
    private BigDecimal quantity;

    /** The amount without tax. */
    private BigDecimal amountWithoutTax;

    /** The amount tax. */
    private BigDecimal amountTax;

    /** The amount with tax. */
    private BigDecimal amountWithTax;

    /** The invoice sub category code. */
    @XmlElement(required = true)
    private String invoiceSubCategoryCode;

    /** The user account code */
    private String userAccountCode;

    /**
     * Amounts broken down by tax
     */
    @XmlElementWrapper
    @XmlElement(name = "amountByTax", required = true)
    private List<SubcategoryInvoiceAgregateAmountDto> amountsByTax;

    /** The rated transactions. */
    @XmlElementWrapper
    @XmlElement(name = "ratedTransaction")
    private List<RatedTransactionDto> ratedTransactions;

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
     * Gets the quantity. Deprecated in v5.2
     *
     * @return the quantity
     */
    @Deprecated
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity. Deprecated in v5.2
     *
     * @param quantity the new quantity
     */
    @Deprecated
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
     * Gets the rated transactions.
     *
     * @return the rated transactions
     */
    public List<RatedTransactionDto> getRatedTransactions() {
        return ratedTransactions;
    }

    /**
     * Sets the rated transactions.
     *
     * @param ratedTransactions the new rated transactions
     */
    public void setRatedTransactions(List<RatedTransactionDto> ratedTransactions) {
        this.ratedTransactions = ratedTransactions;
    }

    /**
     * Gets the invoice sub category code.
     *
     * @return the invoice sub category code
     */
    public String getInvoiceSubCategoryCode() {
        return invoiceSubCategoryCode;
    }

    /**
     * Sets the invoice sub category code.
     *
     * @param invoiceSubCategoryCode the new invoice sub category code
     */
    public void setInvoiceSubCategoryCode(String invoiceSubCategoryCode) {
        this.invoiceSubCategoryCode = invoiceSubCategoryCode;
    }

    /**
     * Gets the user account code.
     *
     * @return the user account code
     */
    public String getUserAccountCode() {
        return userAccountCode;
    }

    /**
     * Sets the user account code.
     *
     * @param userAccountCode the new user account code
     */
    public void setUserAccountCode(String userAccountCode) {
        this.userAccountCode = userAccountCode;
    }

    /**
     * @return Amounts broken down by tax
     */
    public List<SubcategoryInvoiceAgregateAmountDto> getAmountsByTax() {
        return amountsByTax;
    }

    /**
     * @param amountsByTax Amounts broken down by tax
     */
    public void setAmountsByTax(List<SubcategoryInvoiceAgregateAmountDto> amountsByTax) {
        this.amountsByTax = amountsByTax;
    }
}