/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
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
package org.meveo.model.billing;

import org.meveo.model.IBillableEntity;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;

/**
 * Check if any account in the hierarchy (Customer, CA, BA, UA) or subscription and service Instance has a minimum amount to be calculated.
 *
 * @author Khalid HORRI
 * @lastModifiedVersion 10.0
 */
public class MinAmountForAccounts {
    /**
     * Check if Billing account has minimum amount activated.
     */
    private boolean baHasMinAmount = false;
    /**
     * Check if User account has minimum amount activated.
     */
    private boolean uaHasMinAmount = false;
    /**
     * Check if Subscription or OfferTemplate has minimum amount activated.
     */
    private boolean subscriptionHasMinAmount = false;
    /**
     * Check if ServiceInstance or ServiceTemplate has minimum amount activated.
     */
    private boolean serviceHasMinAmount = false;
    /**
     * Check if customer has minimum amount activated.
     */
    private boolean customerHasMinAmount = false;
    /**
     * Check if Customer account has minimum amount activated.
     */
    private boolean customerAccountHasMinAmount = false;

    /**
     * The default constructor
     */
    public MinAmountForAccounts() {
    }

    /**
     * A constructor.
     *
     * @param minRTsUsed an array of booleans
     */
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

    /**
     * A contructor.
     *
     * @param customerHasMinAmount     is customer Has Minimum Amount.
     * @param caHasMinAmount           is customer Account Has Minimum Amount.
     * @param baHasMinAmount           is Billing account Has Minimum Amount.
     * @param uaHasMinAmount           is user account Has Minimum Amount
     * @param subscriptionHasMinAmount is Subscription Has Minimum Amount
     * @param serviceHasMinAmount      is Service Has Minimum Amount
     */
    public MinAmountForAccounts(boolean customerHasMinAmount, boolean caHasMinAmount, boolean baHasMinAmount, boolean uaHasMinAmount, boolean subscriptionHasMinAmount,
            boolean serviceHasMinAmount) {

        this.customerHasMinAmount = customerHasMinAmount;
        this.customerAccountHasMinAmount = caHasMinAmount;
        this.baHasMinAmount = baHasMinAmount;
        this.uaHasMinAmount = uaHasMinAmount;
        this.subscriptionHasMinAmount = subscriptionHasMinAmount;
        this.serviceHasMinAmount = serviceHasMinAmount;
    }

    /**
     * Contructor.
     *
     * @param minRTsUsed           An array of booleans
     * @param entity               a billable entity
     * @param applyMinimumModeEnum an applyMinimumMode Enum
     */
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

    /**
     * Is Billing Account Has Minimum Amount.
     *
     * @return True if Billing Account Has Minimum Amount
     */
    public boolean isBaHasMinAmount() {
        return baHasMinAmount;
    }

    /**
     * Sets baHasMinAmount.
     *
     * @param baHasMinAmount Billing Account Has Minimum Amount
     */
    public void setBaHasMinAmount(boolean baHasMinAmount) {
        this.baHasMinAmount = baHasMinAmount;
    }

    /**
     * Is User Account Has Minimum Amount.
     *
     * @return True if user Account Has Minimum Amount
     */
    public boolean isUaHasMinAmount() {
        return uaHasMinAmount;
    }

    /**
     * Is Subscription Has Minimum Amount.
     *
     * @return True if Subscription Has Minimum Amount
     */
    public boolean isSubscriptionHasMinAmount() {
        return subscriptionHasMinAmount;
    }

    /**
     * Is Service Has Minimum Amount.
     *
     * @return True if Service Has Minimum Amount
     */
    public boolean isServiceHasMinAmount() {
        return serviceHasMinAmount;
    }

    /**
     * Is Customer Has Minimum Amount.
     *
     * @return True if Customer Has Minimum Amount
     */
    public boolean isCustomerHasMinAmount() {
        return customerHasMinAmount;
    }

    /**
     * Is Customer Account Has Minimum Amount.
     *
     * @return True if Customer Account Has Minimum Amount
     */
    public boolean isCustomerAccountHasMinAmount() {
        return customerAccountHasMinAmount;
    }

    /**
     * Check whether Minimum amount is activated in any level of accounts hierarchy.
     *
     * @return a boolean if minimum amount is activatd in any accounts level
     */
    public boolean isMinAmountCalculationActivated() {
        return baHasMinAmount || uaHasMinAmount || subscriptionHasMinAmount || serviceHasMinAmount || customerHasMinAmount || customerAccountHasMinAmount;
    }

    /**
     * check whether Min Amount RT should be included in the first run or not of a billing run.
     *
     * @param includeFirstRun is Min RTs are included in the first run of a billing run
     * @return a MinAmountForAccounts
     */
    public MinAmountForAccounts includesFirstRun(boolean includeFirstRun) {
        MinAmountForAccounts minAmountForAccounts = new MinAmountForAccounts(includeFirstRun && customerHasMinAmount, includeFirstRun && customerAccountHasMinAmount,
                includeFirstRun && baHasMinAmount, includeFirstRun && uaHasMinAmount, includeFirstRun && subscriptionHasMinAmount, includeFirstRun && serviceHasMinAmount);
        return minAmountForAccounts;
    }

    /**
     * Check if Minimum amount is activated in account or a service.
     * @param accountClass the account class.
     * @param entity the billable entity.
     * @return true if Minimum amount is activated false otherwise.
     */
    public boolean isMinAmountForAccountsActivated(@SuppressWarnings("rawtypes") Class accountClass, IBillableEntity entity) {
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
            return customerAccountHasMinAmount && billingAccount.getCustomerAccount().getMinimumTargetAccount() != null && billingAccount.getCustomerAccount().getMinimumTargetAccount().equals(entity);
        }
        if (Customer.class.equals(accountClass)) {
            return customerHasMinAmount && billingAccount.getCustomerAccount().getCustomer().getMinimumTargetAccount() != null && billingAccount.getCustomerAccount().getCustomer().getMinimumTargetAccount().equals(entity);
        }
        return false;
    }
}
