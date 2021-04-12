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
package org.meveo.api.dto.account;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Store all minimum amount information for a CRM account hierarchy.
 *
 * @author Khalid HORRI
 * @lastModifiedVersion 10.0
 */
public class MinimumAmountElDto implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * Expression to determine minimum amount value for Customer.
     */
	@Schema(description = "Expression to determine minimum amount value for Customer")
    private String customerMinimumAmountEl;
    /**
     * Expression to determine rated transaction description to reach minimum amount value for Customer.
     */
	@Schema(description = "Expression to determine rated transaction description to reach minimum amount value for Customer")
    private String customerMinimumLabelEl;
    /**
     * The billable Entity for customer.
     */
	@Schema(description = "The billable Entity for customer")
    private String customerMinimumTargetAccount;

    /**
     * Expression to determine minimum amount value for CA.
     */
	@Schema(description = "Expression to determine minimum amount value for CA")
    private String customerAccountMinimumAmountEl;
    /**
     * Expression to determine rated transaction description to reach minimum amount value for CA.
     */
	@Schema(description = "Expression to determine rated transaction description to reach minimum amount value for CA")
    private String customerAccountMinimumLabelEl;
    /**
     * The billable Entity for CA.
     */
	@Schema(description = "The billable Entity for CA")
    private String customerAccountMinimumTargetAccount;
    /**
     * Expression to determine minimum amount value for BA.
     */
	@Schema(description = "Expression to determine minimum amount value for BA")
    private String billingAccountMinimumAmountEl;
    /**
     * Expression to determine rated transaction description to reach minimum amount value for BA.
     */
	@Schema(description = "Expression to determine rated transaction description to reach minimum amount value for BA")
    private String billingAccountMinimumLabelEl;

    /**
     * Expression to determine minimum amount value for UA.
     */
	@Schema(description = "Expression to determine minimum amount value for UA")
    private String userAccountMinimumAmountEl;
    /**
     * Expression to determine rated transaction description to reach minimum amount value for UA.
     */
	@Schema(description = "Expression to determine rated transaction description to reach minimum amount value for UA")
    private String userAccountMinimumLabelEl;

    /**
     * Corresponding to minimum one shot charge template for the customer
     */
	@Schema(description = "Corresponding to minimum one shot charge template for the customer")
    private String customerMinimumChargeTemplate;

    /**
     * Corresponding to minimum one shot charge template for the customer account
     */
	@Schema(description = "Corresponding to minimum one shot charge template for the customer account")
    private String customerAccountMinimumChargeTemplate;

    /**
     * Corresponding to minimum one shot charge template for the billing account
     */
	@Schema(description = "Corresponding to minimum one shot charge template for the billing account")
    private String billingAccountMinimumChargeTemplate;

    /**
     * Corresponding to minimum one shot charge template for the billing account
     */
	@Schema(description = "Corresponding to minimum one shot charge template for the billing account")
    private String userAccountMinimumChargeTemplate;

    /**
     * Gets customerMinimumAmountEl.
     *
     * @return customerMinimumAmountEl.
     */
    public String getCustomerMinimumAmountEl() {
        return customerMinimumAmountEl;
    }

    /**
     * Sets customerMinimumAmountEl.
     *
     * @param customerMinimumAmountEl customer Minimum Amount El expression
     */
    public void setCustomerMinimumAmountEl(String customerMinimumAmountEl) {
        this.customerMinimumAmountEl = customerMinimumAmountEl;
    }

    /**
     * Gets customerMinimumLabelEl.
     *
     * @return customerMinimumLabelEl
     */
    public String getCustomerMinimumLabelEl() {
        return customerMinimumLabelEl;
    }

    /**
     * Sets customerMinimumLabelEl
     *
     * @param customerMinimumLabelEl customer Minimum Label El expression
     */
    public void setCustomerMinimumLabelEl(String customerMinimumLabelEl) {
        this.customerMinimumLabelEl = customerMinimumLabelEl;
    }

    /**
     * Gets customerMinimumTargetAccount.
     *
     * @return customerMinimumTargetAccount
     */
    public String getCustomerMinimumTargetAccount() {
        return customerMinimumTargetAccount;
    }

    /**
     * Sets customerMinimumTargetAccount
     *
     * @param customerMinimumTargetAccount the billable entity
     */
    public void setCustomerMinimumTargetAccount(String customerMinimumTargetAccount) {
        this.customerMinimumTargetAccount = customerMinimumTargetAccount;
    }

    /**
     * Gets customerAccountMinimumAmountEl.
     *
     * @return customer Account Minimum Amount El expression
     */
    public String getCustomerAccountMinimumAmountEl() {
        return customerAccountMinimumAmountEl;
    }

    /**
     * Sets customerAccountMinimumAmountEl.
     *
     * @param customerAccountMinimumAmountEl customer Account Minimum Amount El expression
     */
    public void setCustomerAccountMinimumAmountEl(String customerAccountMinimumAmountEl) {
        this.customerAccountMinimumAmountEl = customerAccountMinimumAmountEl;
    }

    /**
     * Gets customerAccountMinimumLabelEl.
     *
     * @return customer Account Minimum Label El expression
     */
    public String getCustomerAccountMinimumLabelEl() {
        return customerAccountMinimumLabelEl;
    }

    /**
     * Sets customerAccountMinimumLabelEl.
     *
     * @param customerAccountMinimumLabelEl customer Account Minimum Label El expression
     */
    public void setCustomerAccountMinimumLabelEl(String customerAccountMinimumLabelEl) {
        this.customerAccountMinimumLabelEl = customerAccountMinimumLabelEl;
    }

    /**
     * Gets the billable entity
     *
     * @return the billable entity
     */
    public String getCustomerAccountMinimumTargetAccount() {
        return customerAccountMinimumTargetAccount;
    }

    /**
     * Sets customer Account Minimum Target Account
     *
     * @param customerAccountMinimumTargetAccount the billable entity
     */
    public void setCustomerAccountMinimumTargetAccount(String customerAccountMinimumTargetAccount) {
        this.customerAccountMinimumTargetAccount = customerAccountMinimumTargetAccount;
    }

    /**
     * Gets billing Account Minimum Amount El expression.
     *
     * @return a billing Account Minimum Amount El expression.
     */
    public String getBillingAccountMinimumAmountEl() {
        return billingAccountMinimumAmountEl;
    }

    /**
     * Sets billing Account Minimum Amount El expression.
     *
     * @param billingAccountMinimumAmountEl a billing Account Minimum Amount El expression.
     */
    public void setBillingAccountMinimumAmountEl(String billingAccountMinimumAmountEl) {
        this.billingAccountMinimumAmountEl = billingAccountMinimumAmountEl;
    }

    /**
     * Gets billing Account Minimum Label El expression.
     *
     * @return a billing Account Minimum Label El expression.
     */
    public String getBillingAccountMinimumLabelEl() {
        return billingAccountMinimumLabelEl;
    }

    /**
     * Sets billing Account Minimum Label El expression.
     *
     * @param billingAccountMinimumLabelEl a billing Account Minimum Label El expression.
     */
    public void setBillingAccountMinimumLabelEl(String billingAccountMinimumLabelEl) {
        this.billingAccountMinimumLabelEl = billingAccountMinimumLabelEl;
    }

    /**
     * Gets user Account Minimum Amount El expression.
     *
     * @return userAccountMinimumAmountEl.
     */
    public String getUserAccountMinimumAmountEl() {
        return userAccountMinimumAmountEl;
    }

    /**
     * Sets user Account Minimum Amount El expression.
     *
     * @param userAccountMinimumAmountEl a user Account Minimum Amount El expression.
     */
    public void setUserAccountMinimumAmountEl(String userAccountMinimumAmountEl) {
        this.userAccountMinimumAmountEl = userAccountMinimumAmountEl;
    }

    /**
     * Gets user Account Minimum Label El expression.
     *
     * @return userAccountMinimumLabelEl
     */
    public String getUserAccountMinimumLabelEl() {
        return userAccountMinimumLabelEl;
    }

    /**
     * Sets user Account Minimum Label El expression.
     *
     * @param userAccountMinimumLabelEl a user Account Minimum Label El expression.
     */

    public void setUserAccountMinimumLabelEl(String userAccountMinimumLabelEl) {
        this.userAccountMinimumLabelEl = userAccountMinimumLabelEl;
    }

    public String getCustomerMinimumChargeTemplate() {
        return customerMinimumChargeTemplate;
    }

    public void setCustomerMinimumChargeTemplate(String customerMinimumChargeTemplate) {
        this.customerMinimumChargeTemplate = customerMinimumChargeTemplate;
    }

    public String getCustomerAccountMinimumChargeTemplate() {
        return customerAccountMinimumChargeTemplate;
    }

    public void setCustomerAccountMinimumChargeTemplate(String customerAccountMinimumChargeTemplate) {
        this.customerAccountMinimumChargeTemplate = customerAccountMinimumChargeTemplate;
    }

    public String getBillingAccountMinimumChargeTemplate() {
        return billingAccountMinimumChargeTemplate;
    }

    public void setBillingAccountMinimumChargeTemplate(String billingAccountMinimumChargeTemplate) {
        this.billingAccountMinimumChargeTemplate = billingAccountMinimumChargeTemplate;
    }

    public String getUserAccountMinimumChargeTemplate() {
        return userAccountMinimumChargeTemplate;
    }

    public void setUserAccountMinimumChargeTemplate(String userAccountMinimumChargeTemplate) {
        this.userAccountMinimumChargeTemplate = userAccountMinimumChargeTemplate;
    }
}
