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

package org.meveo.api.dto.payment;

public class HostedCheckoutInput {


    String locale;
    String amount;
    String currencyCode;
    String authorizationMode;
    String countryCode;
    boolean skipAuthentication;
    String customerAccountCode;
    long customerAccountId;
    String returnUrl;
    String variant;
    GatewayPaymentNamesEnum gatewayPaymentName;

    public GatewayPaymentNamesEnum getGatewayPaymentName() {
        return gatewayPaymentName;
    }

    public void setGatewayPaymentName(GatewayPaymentNamesEnum gatewayPaymentName) {
        this.gatewayPaymentName = gatewayPaymentName;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getAuthorizationMode() {
        return authorizationMode;
    }

    public void setAuthorizationMode(String authorizationMode) {
        this.authorizationMode = authorizationMode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public boolean isSkipAuthentication() {
        return skipAuthentication;
    }

    public void setSkipAuthentication(boolean skipAuthentication) {
        this.skipAuthentication = skipAuthentication;
    }

    public String getCustomerAccountCode() {
        return customerAccountCode;
    }

    public void setCustomerAccountCode(String customerAccountCode) {
        this.customerAccountCode = customerAccountCode;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public long getCustomerAccountId() {
        return customerAccountId;
    }

    public void setCustomerAccountId(long customerAccountId) {
        this.customerAccountId = customerAccountId;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }
}
