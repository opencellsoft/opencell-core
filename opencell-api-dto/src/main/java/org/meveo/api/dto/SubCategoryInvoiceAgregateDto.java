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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.SubCategoryInvoiceAgregate;

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

    /** The type. */
    private String type;

    /** The item number. */
    private Integer itemNumber;

    /** The accounting code. */
    private String accountingCode;

    /** The description. */
    private String description;

    /** The tax percent. */
    private BigDecimal taxPercent;

    /** The quantity. */
    private BigDecimal quantity;

    /** The discount. */
    private BigDecimal discount;

    /** The amount without tax. */
    private BigDecimal amountWithoutTax;

    /** The amount tax. */
    private BigDecimal amountTax;

    /** The amount with tax. */
    private BigDecimal amountWithTax;

    /** The invoice sub category code. */
    @XmlElement(required = true)
    private String invoiceSubCategoryCode;

    /** The taxes codes. */
    private List<String> taxesCodes = new ArrayList<String>();

    /** The user account code. */
    private String userAccountCode;

    /** The rated transactions. */
    @XmlElementWrapper
    @XmlElement(name = "ratedTransaction")
    private List<RatedTransactionDto> ratedTransactions = new ArrayList<RatedTransactionDto>();

    /** The discount plan code. */
    private String discountPlanCode;

    /** The discount plan item code. */
    private String discountPlanItemCode;

    /** The discount percent. */
    private BigDecimal discountPercent;

    /**
     * Instantiates a new sub category invoice agregate dto.
     *
     * @param subCategoryInvoiceAgregate the SubCategoryInvoiceAgregate entity
     */
    public SubCategoryInvoiceAgregateDto(SubCategoryInvoiceAgregate subCategoryInvoiceAgregate) {
        if (subCategoryInvoiceAgregate != null) {
            discountPlanCode = subCategoryInvoiceAgregate.getDiscountPlanItem().getDiscountPlan().getCode();
            discountPlanItemCode = subCategoryInvoiceAgregate.getDiscountPlanItem().getCode();
            discountPercent = subCategoryInvoiceAgregate.getDiscountPercent();
            itemNumber = subCategoryInvoiceAgregate.getItemNumber();
            if (subCategoryInvoiceAgregate.getAccountingCode() != null) {
                accountingCode = subCategoryInvoiceAgregate.getAccountingCode().getCode();
            }
            description = subCategoryInvoiceAgregate.getDescription();
            taxPercent = subCategoryInvoiceAgregate.getTaxPercent();
            quantity = subCategoryInvoiceAgregate.getQuantity();
            amountWithoutTax = subCategoryInvoiceAgregate.getAmountWithoutTax();
            amountTax = subCategoryInvoiceAgregate.getAmountTax();
            amountWithTax = subCategoryInvoiceAgregate.getAmountTax();

            if (subCategoryInvoiceAgregate.getInvoiceSubCategory() != null) {
                invoiceSubCategoryCode = subCategoryInvoiceAgregate.getInvoiceSubCategory().getCode();
            }
            if (subCategoryInvoiceAgregate.getUserAccount() != null) {
                userAccountCode = subCategoryInvoiceAgregate.getUserAccount().getCode();
            }
        }
    }

    /**
     * Instantiates a new sub category invoice agregate dto.
     */
    public SubCategoryInvoiceAgregateDto() {

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
     * Gets the tax percent.
     *
     * @return the tax percent
     */
    public BigDecimal getTaxPercent() {
        return taxPercent;
    }

    /**
     * Sets the tax percent.
     *
     * @param taxPercent the new tax percent
     */
    public void setTaxPercent(BigDecimal taxPercent) {
        this.taxPercent = taxPercent;
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
     * Gets the discount.
     *
     * @return the discount
     */
    public BigDecimal getDiscount() {
        return discount;
    }

    /**
     * Sets the discount.
     *
     * @param discount the new discount
     */
    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
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
     * Gets the type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the new type
     */
    public void setType(String type) {
        this.type = type;
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
     * Gets the taxes codes.
     *
     * @return the taxes codes
     */
    public List<String> getTaxesCodes() {
        return taxesCodes;
    }

    /**
     * Sets the taxes codes.
     *
     * @param taxesCodes the new taxes codes
     */
    public void setTaxesCodes(List<String> taxesCodes) {
        this.taxesCodes = taxesCodes;
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
     * Gets the discount plan code.
     *
     * @return the discount plan code
     */
    public String getDiscountPlanCode() {
        return discountPlanCode;
    }

    /**
     * Sets the discount plan code.
     *
     * @param discountPlanCode the new discount plan code
     */
    public void setDiscountPlanCode(String discountPlanCode) {
        this.discountPlanCode = discountPlanCode;
    }

    /**
     * Gets the discount plan item code.
     *
     * @return the discount plan item code
     */
    public String getDiscountPlanItemCode() {
        return discountPlanItemCode;
    }

    /**
     * Sets the discount plan item code.
     *
     * @param discountPlanItemCode the new discount plan item code
     */
    public void setDiscountPlanItemCode(String discountPlanItemCode) {
        this.discountPlanItemCode = discountPlanItemCode;
    }

    /**
     * Gets the discount percent.
     *
     * @return the discount percent
     */
    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    /**
     * Sets the discount percent.
     *
     * @param discountPercent the new discount percent
     */
    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }
}
