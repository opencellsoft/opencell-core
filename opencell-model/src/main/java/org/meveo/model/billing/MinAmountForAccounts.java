package org.meveo.model.billing;

import org.meveo.model.IBillableEntity;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;

/**
 * Check if any account in the hierarchy or subscription and service Instance has a minimum amount to be calculated
 * @author Khalid HORRI
 * @lastModifiedVersion 10.0
 */
public class MinAmountForAccounts {
    /**
     * Check if Billing account has minimum amount activated
     */
    private boolean baHasMinAmount = false;
    /**
     * Check if User account has minimum amount activated
     */
    private boolean uaHasMinAmount = false;
    /**
     * Check if Subscription or OfferTemplate has minimum amount activated
     */
    private boolean subscriptionHasMinAmount = false;
    /**
     * Check if ServiceInstance or ServiceTemplate has minimum amount activated
     */
    private boolean serviceHasMinAmount = false;
    /**
     * Check if customer has minimum amount activated
     */
    private boolean customerHasMinAmount = false;
    /**
     * Check if Customer account has minimum amount activated
     */
    private boolean customerAccountHasMinAmount = false;

    public MinAmountForAccounts() {
    }

    public MinAmountForAccounts(boolean[] minRTsUsed) {
        if (minRTsUsed != null && minRTsUsed.length > 5) {
            serviceHasMinAmount = minRTsUsed[0];
            subscriptionHasMinAmount = minRTsUsed[1];
            uaHasMinAmount = minRTsUsed[2];
            baHasMinAmount = minRTsUsed[3];
            customerAccountHasMinAmount = minRTsUsed[4];
            customerHasMinAmount = minRTsUsed[5];
        }
    }

    public MinAmountForAccounts(boolean customerHasMinAmount, boolean caHasMinAmount, boolean baHasMinAmount, boolean uaHasMinAmount, boolean subscriptionHasMinAmount,
            boolean serviceHasMinAmount) {

        this.customerHasMinAmount = customerHasMinAmount;
        this.customerAccountHasMinAmount = caHasMinAmount;
        this.baHasMinAmount = baHasMinAmount;
        this.uaHasMinAmount = uaHasMinAmount;
        this.subscriptionHasMinAmount = subscriptionHasMinAmount;
        this.serviceHasMinAmount = serviceHasMinAmount;
    }


    public MinAmountForAccounts(boolean[] minRTsUsed, IBillableEntity entity, ApplyMinimumModeEnum applyMinimumModeEnum) {

        if (applyMinimumModeEnum.equals(ApplyMinimumModeEnum.NONE)) {
            serviceHasMinAmount = false;
            subscriptionHasMinAmount = false;
            uaHasMinAmount = false;
            baHasMinAmount = false;
            customerAccountHasMinAmount = false;
            customerHasMinAmount = false;
        }
        if (applyMinimumModeEnum.equals(ApplyMinimumModeEnum.ALL) && minRTsUsed != null && minRTsUsed.length > 5) {
            serviceHasMinAmount = minRTsUsed[0];
            subscriptionHasMinAmount = minRTsUsed[1];
            uaHasMinAmount = minRTsUsed[2];
            baHasMinAmount = minRTsUsed[3];
            customerAccountHasMinAmount = minRTsUsed[4];
            customerHasMinAmount = minRTsUsed[5];
        }
        if (applyMinimumModeEnum.equals(ApplyMinimumModeEnum.NO_PARENT) && minRTsUsed != null && minRTsUsed.length > 5) {

            serviceHasMinAmount = minRTsUsed[2];
            subscriptionHasMinAmount = minRTsUsed[1];
            if (entity instanceof Subscription) {
                uaHasMinAmount = false;
                baHasMinAmount = false;
                customerAccountHasMinAmount = false;
                customerHasMinAmount = false;
            } else if (entity instanceof BillingAccount) {
                uaHasMinAmount = minRTsUsed[2];
                baHasMinAmount = minRTsUsed[3];
                customerAccountHasMinAmount = false;
                customerHasMinAmount = false;
            }

        }

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

    /**
     * Check whether Min amount is activated on any level of accounts hierarchy
     *
     * @return
     */
    public boolean isMinAmountCalculationActivated() {
        return baHasMinAmount || uaHasMinAmount || subscriptionHasMinAmount || serviceHasMinAmount || customerHasMinAmount || customerAccountHasMinAmount;
    }

    /**
     *
     * @param includeFirstRun
     * @return
     */
    public MinAmountForAccounts includesFirstRun(boolean includeFirstRun) {
        MinAmountForAccounts minAmountForAccounts = new MinAmountForAccounts(includeFirstRun && customerHasMinAmount, includeFirstRun && customerAccountHasMinAmount,
                includeFirstRun && baHasMinAmount, includeFirstRun && uaHasMinAmount, includeFirstRun && subscriptionHasMinAmount, includeFirstRun && serviceHasMinAmount);
        return minAmountForAccounts;
    }

    /**
     * Check if Minimum amount is activated in account.
     * @param accountClass the account class.
     * @param entity the billable entity.
     * @return true if Minimum amount is activated false otherwise.
     */
    public boolean isMinAmountForAccountsActivated(Class accountClass, IBillableEntity entity) {
        BillingAccount billingAccount = null;
        if (entity instanceof Subscription) {
            billingAccount = ((Subscription) entity).getUserAccount().getBillingAccount();
        } else {
            billingAccount = (BillingAccount) entity;
        }
        if (ServiceInstance.class.equals(accountClass)) {
            return serviceHasMinAmount;
        }
        if (Subscription.class.equals(accountClass)) {
            return subscriptionHasMinAmount;
        }
        if (UserAccount.class.equals(accountClass)) {
            return uaHasMinAmount;
        }
        if (BillingAccount.class.equals(accountClass)) {
            return baHasMinAmount;
        }
        if (CustomerAccount.class.equals(accountClass)) {
            return customerAccountHasMinAmount && billingAccount.getCustomerAccount().getMinimumTargetAccount() != null && billingAccount.getCustomerAccount()
                    .getMinimumTargetAccount().equals(entity);
        }
        if (Customer.class.equals(accountClass)) {
            return customerHasMinAmount && billingAccount.getCustomerAccount().getCustomer().getMinimumTargetAccount() != null && billingAccount.getCustomerAccount().getCustomer()
                    .getMinimumTargetAccount().equals(entity);
        }
        return false;
    }
}
