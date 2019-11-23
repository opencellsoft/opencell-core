package org.meveo.model.billing;

public class MinAmountForAccounts {

    private boolean baHasMinAmount = false;
    private boolean uaHasMinAmount = false;
    private boolean subscriptionHasMinAmount = false;
    private boolean serviceHasMinAmount = false;
    private boolean customerHasMinAmount = false;
    private boolean customerAccountHasMinAmount = false;

    public MinAmountForAccounts() {
    }

    public MinAmountForAccounts(boolean[] minRTsUsed) {
        if (minRTsUsed != null && minRTsUsed.length > 5) {
            baHasMinAmount = minRTsUsed[0];
            subscriptionHasMinAmount = minRTsUsed[1];
            serviceHasMinAmount = minRTsUsed[2];
            uaHasMinAmount = minRTsUsed[3];
            customerAccountHasMinAmount = minRTsUsed[4];
            customerHasMinAmount = minRTsUsed[5];
        }
    }

    public MinAmountForAccounts(boolean baHasMinAmount, boolean uaHasMinAmount, boolean subscriptionHasMinAmount, boolean serviceHasMinAmount) {
        this.baHasMinAmount = baHasMinAmount;
        this.uaHasMinAmount = uaHasMinAmount;
        this.subscriptionHasMinAmount = subscriptionHasMinAmount;
        this.serviceHasMinAmount = serviceHasMinAmount;
    }

    public boolean isBaHasMinAmount() {
        return baHasMinAmount;
    }

    public void setBaHasMinAmount(boolean baHasMinAmount) {
        this.baHasMinAmount = baHasMinAmount;
    }

    public boolean isUaHasMinAmount() {
        return uaHasMinAmount;
    }

    public void setUaHasMinAmount(boolean uaHasMinAmount) {
        this.uaHasMinAmount = uaHasMinAmount;
    }

    public boolean isSubscriptionHasMinAmount() {
        return subscriptionHasMinAmount;
    }

    public void setSubscriptionHasMinAmount(boolean subscriptionHasMinAmount) {
        this.subscriptionHasMinAmount = subscriptionHasMinAmount;
    }

    public boolean isServiceHasMinAmount() {
        return serviceHasMinAmount;
    }

    public void setServiceHasMinAmount(boolean serviceHasMinAmount) {
        this.serviceHasMinAmount = serviceHasMinAmount;
    }

    public boolean isCustomerHasMinAmount() {
        return customerHasMinAmount;
    }

    public void setCustomerHasMinAmount(boolean customerHasMinAmount) {
        this.customerHasMinAmount = customerHasMinAmount;
    }

    public boolean isCustomerAccountHasMinAmount() {
        return customerAccountHasMinAmount;
    }

    public void setCustomerAccountHasMinAmount(boolean customerAccountHasMinAmount) {
        this.customerAccountHasMinAmount = customerAccountHasMinAmount;
    }
}
