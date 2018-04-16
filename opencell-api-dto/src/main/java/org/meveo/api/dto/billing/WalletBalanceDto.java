package org.meveo.api.dto.billing;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

/**
 * Parameters to calculate wallet balance. Seller, customer, customer account, billing account and user account code parameters are mutually exclusive and only one of them should
 * be provided.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0.1
 **/
@XmlRootElement(name = "WalletBalance")
@XmlAccessorType(XmlAccessType.FIELD)
public class WalletBalanceDto extends BaseDto {

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

    /**
     * What amount to calculate - amount with tax if value is TRUE, amount witout tax if value is FALSE. Not used since v. 5.0.1. Wallet API returns both amounts
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
}