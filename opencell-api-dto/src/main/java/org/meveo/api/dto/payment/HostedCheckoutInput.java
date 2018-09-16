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
}
