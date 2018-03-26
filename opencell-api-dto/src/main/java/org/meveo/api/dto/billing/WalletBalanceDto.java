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

    private static final long serialVersionUID = 2275297081429778741L;

    /**
     * Seller code.
     */
    private String sellerCode;

    /**
     * Customer code
     */
    private String customerCode;

    /**
     * Customer account code
     */
    private String customerAccountCode;

    /**
     * Billing account code
     */
    private String billingAccountCode;

    /**
     * User account code
     */
    private String userAccountCode;

    /**
     * Date period to calculate balance: from
     */
    private Date startDate;

    /**
     * Date period to calculate balance: to
     */
    private Date endDate;

    /**
     * What amount to calculate - amount with tax if value is TRUE, amount witout tax if value is FALSE. Not used since v. 5.0.1. Wallet API returns both amounts
     */
    @Deprecated
    private Boolean amountWithTax;

    public String getSellerCode() {
        return sellerCode;
    }

    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerAccountCode() {
        return customerAccountCode;
    }

    public void setCustomerAccountCode(String customerAccountCode) {
        this.customerAccountCode = customerAccountCode;
    }

    public String getBillingAccountCode() {
        return billingAccountCode;
    }

    public void setBillingAccountCode(String billingAccountCode) {
        this.billingAccountCode = billingAccountCode;
    }

    public String getUserAccountCode() {
        return userAccountCode;
    }

    public void setUserAccountCode(String userAccountCode) {
        this.userAccountCode = userAccountCode;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Boolean isAmountWithTax() {
        return amountWithTax;
    }

    public void setAmountWithTax(Boolean amountWithTax) {
        this.amountWithTax = amountWithTax;
    }
}