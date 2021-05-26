package org.meveo.api.dto.payment;


/**
 * The Class HostedCheckoutInput.
 */
public class HostedCheckoutInput {

    /**
     * The locale.
     */
    String locale;

    /**
     * The amount.
     */
    String amount;

    /**
     * The currency code.
     */
    String currencyCode;

    /**
     * The authorization mode.
     */
    String authorizationMode;

    /**
     * The country code.
     */
    String countryCode;

    /**
     * The skip authentication.
     */
    boolean skipAuthentication;

    /**
     * The customer account code.
     */
    String customerAccountCode;

    /**
     * The customer account id.
     */
    long customerAccountId;

    /**
     * The return url.
     */
    String returnUrl;

    /**
     * The automaticReturnUrl (currently only for ATOS Wallet).
     */
    String automaticReturnUrl;

    /**
     * The variant.
     */
    String variant;

    /**
     * The gateway payment name.
     */
    GatewayPaymentNamesEnum gatewayPaymentName;

    /**
     * The seller code.
     */
    String sellerCode;

    /**
     * The allowed actions (currently only for {@link }).
     */
    String allowedActions;

    /**
     * The return context (currently only for ATOS Wallet).
     */
    String returnContext;

    /**
     * The advanced options (currently only for ATOS Wallet).
     */
    String advancedOptions;

    /**
     * Gets the gateway payment name.
     *
     * @return the gateway payment name
     */
    public GatewayPaymentNamesEnum getGatewayPaymentName() {
        return gatewayPaymentName;
    }

    /**
     * Sets the gateway payment name.
     *
     * @param gatewayPaymentName the new gateway payment name
     */
    public void setGatewayPaymentName(GatewayPaymentNamesEnum gatewayPaymentName) {
        this.gatewayPaymentName = gatewayPaymentName;
    }

    /**
     * Gets the locale.
     *
     * @return the locale
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Sets the locale.
     *
     * @param locale the new locale
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     * Gets the amount.
     *
     * @return the amount
     */
    public String getAmount() {
        return amount;
    }

    /**
     * Sets the amount.
     *
     * @param amount the new amount
     */
    public void setAmount(String amount) {
        this.amount = amount;
    }

    /**
     * Gets the currency code.
     *
     * @return the currency code
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * Sets the currency code.
     *
     * @param currencyCode the new currency code
     */
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    /**
     * Gets the authorization mode.
     *
     * @return the authorization mode
     */
    public String getAuthorizationMode() {
        return authorizationMode;
    }

    /**
     * Sets the authorization mode.
     *
     * @param authorizationMode the new authorization mode
     */
    public void setAuthorizationMode(String authorizationMode) {
        this.authorizationMode = authorizationMode;
    }

    /**
     * Gets the country code.
     *
     * @return the country code
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Sets the country code.
     *
     * @param countryCode the new country code
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * Checks if is skip authentication.
     *
     * @return true, if is skip authentication
     */
    public boolean isSkipAuthentication() {
        return skipAuthentication;
    }

    /**
     * Sets the skip authentication.
     *
     * @param skipAuthentication the new skip authentication
     */
    public void setSkipAuthentication(boolean skipAuthentication) {
        this.skipAuthentication = skipAuthentication;
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
     * Gets the return url.
     *
     * @return the return url
     */
    public String getReturnUrl() {
        return returnUrl;
    }

    /**
     * Sets the return url.
     *
     * @param returnUrl the new return url
     */
    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    /**
     * Gets the automatic return url.
     *
     * @return the automatic return url
     */
    public String getAutomaticReturnUrl() {
        return automaticReturnUrl;
    }

    /**
     * Sets the automatic return url.
     *
     * @param automaticReturnUrl the new automatic return url
     */
    public void setAutomaticReturnUrl(String automaticReturnUrl) {
        this.automaticReturnUrl = automaticReturnUrl;
    }

    /**
     * Gets the customer account id.
     *
     * @return the customer account id
     */
    public long getCustomerAccountId() {
        return customerAccountId;
    }

    /**
     * Sets the customer account id.
     *
     * @param customerAccountId the new customer account id
     */
    public void setCustomerAccountId(long customerAccountId) {
        this.customerAccountId = customerAccountId;
    }

    /**
     * Gets the variant.
     *
     * @return the variant
     */
    public String getVariant() {
        return variant;
    }

    /**
     * Sets the variant.
     *
     * @param variant the new variant
     */
    public void setVariant(String variant) {
        this.variant = variant;
    }

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
     * Gets the allowed actions.
     *
     * @return the allowed actions
     */
    public String getAllowedActions() {
        return allowedActions;
    }

    /**
     * Sets the allowed actions.
     *
     * @param allowedActions the new allowed actions
     */
    public void setAllowedActions(String allowedActions) {
        this.allowedActions = allowedActions;
    }

    /**
     * Gets the return context.
     *
     * @return the return context
     */
    public String getReturnContext() {
        return returnContext;
    }

    /**
     * Sets the return context.
     *
     * @param returnContext the new return context
     */
    public void setReturnContext(String returnContext) {
        this.returnContext = returnContext;
    }

    /**
     * Gets advanced options.
     *
     * @return advanced options
     */
    public String getAdvancedOptions() {
        return advancedOptions;
    }

    /**
     * Sets advanced options.
     *
     * @param advancedOptions new advanced options
     */
    public void setAdvancedOptions(String advancedOptions) {
        this.advancedOptions = advancedOptions;
    }
}
