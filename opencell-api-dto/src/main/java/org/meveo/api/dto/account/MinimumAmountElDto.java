package org.meveo.api.dto.account;

import java.io.Serializable;

public class MinimumAmountElDto implements Serializable {

    private String customerMinimumAmountEl;
    private String customerMinimumLabelEl;
    private String customerMinimumTargetAccount;

    private String customerAccountMinimumAmountEl;
    private String customerAccountMinimumLabelEl;
    private String customerAccountMinimumTargetAccount;

    private String billingAccountMinimumAmountEl;
    private String billingAccountMinimumLabelEl;

    private String userAccountMinimumAmountEl;
    private String userAccountMinimumLabelEl;

    public String getCustomerMinimumAmountEl() {
        return customerMinimumAmountEl;
    }

    public void setCustomerMinimumAmountEl(String customerMinimumAmountEl) {
        this.customerMinimumAmountEl = customerMinimumAmountEl;
    }

    public String getCustomerMinimumLabelEl() {
        return customerMinimumLabelEl;
    }

    public void setCustomerMinimumLabelEl(String customerMinimumLabelEl) {
        this.customerMinimumLabelEl = customerMinimumLabelEl;
    }

    public String getCustomerMinimumTargetAccount() {
        return customerMinimumTargetAccount;
    }

    public void setCustomerMinimumTargetAccount(String customerMinimumTargetAccount) {
        this.customerMinimumTargetAccount = customerMinimumTargetAccount;
    }

    public String getCustomerAccountMinimumAmountEl() {
        return customerAccountMinimumAmountEl;
    }

    public void setCustomerAccountMinimumAmountEl(String customerAccountMinimumAmountEl) {
        this.customerAccountMinimumAmountEl = customerAccountMinimumAmountEl;
    }

    public String getCustomerAccountMinimumLabelEl() {
        return customerAccountMinimumLabelEl;
    }

    public void setCustomerAccountMinimumLabelEl(String customerAccountMinimumLabelEl) {
        this.customerAccountMinimumLabelEl = customerAccountMinimumLabelEl;
    }

    public String getCustomerAccountMinimumTargetAccount() {
        return customerAccountMinimumTargetAccount;
    }

    public void setCustomerAccountMinimumTargetAccount(String customerAccountMinimumTargetAccount) {
        this.customerAccountMinimumTargetAccount = customerAccountMinimumTargetAccount;
    }

    public String getBillingAccountMinimumAmountEl() {
        return billingAccountMinimumAmountEl;
    }

    public void setBillingAccountMinimumAmountEl(String billingAccountMinimumAmountEl) {
        this.billingAccountMinimumAmountEl = billingAccountMinimumAmountEl;
    }

    public String getBillingAccountMinimumLabelEl() {
        return billingAccountMinimumLabelEl;
    }

    public void setBillingAccountMinimumLabelEl(String billingAccountMinimumLabelEl) {
        this.billingAccountMinimumLabelEl = billingAccountMinimumLabelEl;
    }

    public String getUserAccountMinimumAmountEl() {
        return userAccountMinimumAmountEl;
    }

    public void setUserAccountMinimumAmountEl(String userAccountMinimumAmountEl) {
        this.userAccountMinimumAmountEl = userAccountMinimumAmountEl;
    }

    public String getUserAccountMinimumLabelEl() {
        return userAccountMinimumLabelEl;
    }

    public void setUserAccountMinimumLabelEl(String userAccountMinimumLabelEl) {
        this.userAccountMinimumLabelEl = userAccountMinimumLabelEl;
    }
}
