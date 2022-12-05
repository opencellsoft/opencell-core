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

import java.util.Date;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;

/**
 * Parameters to calculate wallet balance. Seller, customer, customer account, billing account and user account code parameters are mutually exclusive and only one of them should
 * be provided.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0.1
 **/
@XmlRootElement(name = "WalletBalance")
@XmlAccessorType(XmlAccessType.FIELD)
public class WalletBalanceDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2275297081429778741L;

    /**
     * Seller code.
     */
    private String sellerCode;

    /** Customer code. */
    private String customerCode;

    /** Customer account code. */
    private String customerAccountCode;

    /** Billing account code. */
    private String billingAccountCode;

    /** User account code. */
    private String userAccountCode;

    /** Date period to calculate balance: from. */
    private Date startDate;

    /** Date period to calculate balance: to. */
    private Date endDate;

    /** Wallet template code. */
    private String walletCode;

    /**
     * What amount to calculate - amount with tax if value is TRUE, amount without tax if value is FALSE. Not used since v. 5.0.1. Wallet API returns both amounts
     */
    @Deprecated
    private Boolean amountWithTax;

    /**
     * Gets the seller code.
     *
     * @return the seller code
     */
    public String getSellerCode() {
        return sellerCode;
    }

    /**
     * Sets the seller code.
     *
     * @param sellerCode the new seller code
     */
    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
    }

    /**
     * Gets the customer code.
     *
     * @return the customer code
     */
    public String getCustomerCode() {
        return customerCode;
    }

    /**
     * Sets the customer code.
     *
     * @param customerCode the new customer code
     */
    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    /**
     * Gets the customer account code.
     *
     * @return the customer account code
     */
    public String getCustomerAccountCode() {
        return customerAccountCode;
    }

    /**
     * Sets the customer account code.
     *
     * @param customerAccountCode the new customer account code
     */
    public void setCustomerAccountCode(String customerAccountCode) {
        this.customerAccountCode = customerAccountCode;
    }

    /**
     * Gets the billing account code.
     *
     * @return the billing account code
     */
    public String getBillingAccountCode() {
        return billingAccountCode;
    }

    /**
     * Sets the billing account code.
     *
     * @param billingAccountCode the new billing account code
     */
    public void setBillingAccountCode(String billingAccountCode) {
        this.billingAccountCode = billingAccountCode;
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
     * @param startDate the new start date
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
     * @param endDate the new end date
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Checks if is amount with tax.
     *
     * @return the boolean
     */
    public Boolean isAmountWithTax() {
        return amountWithTax;
    }

    /**
     * Sets the amount with tax.
     *
     * @param amountWithTax the new amount with tax
     */
    public void setAmountWithTax(Boolean amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    /**
     * @return Wallet template code
     */
    public String getWalletCode() {
        return walletCode;
    }

    /**
     * @param walletCode Wallet template code
     */
    public void setWalletCode(String walletCode) {
        this.walletCode = walletCode;
    }
}