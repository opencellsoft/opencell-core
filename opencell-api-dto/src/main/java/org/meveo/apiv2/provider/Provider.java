package org.meveo.apiv2.provider;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.*;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.payments.PaymentPlanPolicy;

import javax.annotation.Nullable;

import java.util.Map;
import java.util.Set;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(builder = ImmutableProvider.Builder.class)
public interface Provider {

    @Nullable
    String getDescription();
    @Nullable Boolean getActive();
    @Nullable Boolean getMultiCountryFlag();
    @Nullable Boolean getMultiCurrencyFlag();
    @Nullable Boolean getMultiLanguageFlag();
    @Nullable Boolean getEnterprise();
    @Nullable Boolean getLevelDuplication();
    @Nullable Boolean getRecognizeRevenue();
    @Nullable Boolean getAutomaticInvoicing();
    @Nullable Boolean getAmountValidation();
    @Nullable Boolean getDisplayFreeTransacInInvoice();
    @Nullable Boolean getPaymentDeferral();
    @Nullable Boolean getPaymentPlan();
    @Nullable Integer getRounding();
    @Nullable Integer getInvoiceRounding();
    @Nullable Long getPrepaidReservationExpirationDelayinMillisec();
    @Nullable String getRoundingMode();
    @Nullable String getInvoiceRoundingMode();
    @Nullable String getDiscountAccountingCode();
    @Nullable String getEmail();
    @Nullable Integer getMaximumDelay();
    @Nullable Integer getMaximumDeferralPerInvoice();
    @Nullable String getPortalMessage();
    @Nullable String getCurrentMatchingCode();
    @Nullable String getIsoICDCode();
    
    @Nullable
    Map<String, Long> getCurrency();

    @Nullable
    Map<String, Long> getCountry();

    @Nullable
    Map<String, Long> getLanguage();

    @Nullable
    Map<String, Long> getCustomer();

    @Nullable
    Map<String, Long> getCustomerAccount();

    @Nullable
    Map<String, Long> getBillingAccount();

    @Nullable
    Map<String, Long> getUserAccount();

    @Nullable
    Map<String, Long> getInvoiceConfiguration();

    @Nullable
    BankCoordinates getBankCoordinates();

    @Nullable
    PaymentPlanPolicy getPaymentPlanPolicy();
    
    @Nullable
    Set<String> getPaymentMethods();

    @Nullable
    Set<String> getAllowedManualRefundMethods();
    
    @Nullable
    Set<String> getOrderLineTypes();


    default org.meveo.model.crm.Provider toEntity() {

        org.meveo.model.crm.Provider provider = new org.meveo.model.crm.Provider();

        if (!StringUtils.isBlank(this.getDescription())) {
            provider.setDescription(this.getDescription());
        }
        if (this.getCurrency() != null) {
            Currency currency = new Currency();
            currency.setId(getCurrency().get("id"));
            provider.setCurrency(currency);
        }
        if (this.getCountry() != null) {
            Country country = new Country();
            country.setId(getCountry().get("id"));
            provider.setCountry(country);
        }
        if (this.getLanguage() != null) {
            Language language = new Language();
            language.setId(getLanguage().get("id"));
            provider.setLanguage(language);
        }
        if (this.getMultiCurrencyFlag() != null) {
            provider.setMulticurrencyFlag(this.getMultiCurrencyFlag());
        }
        if (this.getMultiCountryFlag() != null) {
            provider.setMulticountryFlag(this.getMultiCountryFlag());
        }
        if (this.getMultiLanguageFlag() != null) {
            provider.setMultilanguageFlag(this.getMultiLanguageFlag());
        }
        if (this.getUserAccount() != null) {
            UserAccount userAccount = new UserAccount();
            userAccount.setId(getUserAccount().get("id"));
        }
        if (this.getEnterprise() != null) {
            provider.setEntreprise(this.getEnterprise());
        }
        if (this.getLevelDuplication() != null) {
            provider.setLevelDuplication(this.getLevelDuplication());
        }
        if (this.getRounding() != null) {
            provider.setRounding(this.getRounding());
        }
        if (this.getRoundingMode() != null) {
            provider.setRoundingMode(RoundingModeEnum.valueOf(this.getRoundingMode()));
        }
        if (this.getInvoiceRounding() != null) {
            provider.setInvoiceRounding(this.getInvoiceRounding());
        }

        if(getAutomaticInvoicing() != null) {
            provider.setRecognizeRevenue(getAutomaticInvoicing());
        }

        if(getAmountValidation() != null) {
            provider.setAmountValidation(getAmountValidation());
        }

        if(getDisplayFreeTransacInInvoice() != null) {
            provider.setDisplayFreeTransacInInvoice(getDisplayFreeTransacInInvoice());
        }

        if(getPaymentDeferral() != null) {
            provider.setPaymentDeferral(getPaymentDeferral());
        }

        if(getPaymentPlan() != null) {
            provider.setPaymentPlan(getPaymentPlan());
        }

        if (this.getInvoiceRoundingMode() != null) {
            provider.setInvoiceRoundingMode(RoundingModeEnum.valueOf(this.getInvoiceRoundingMode()));
        }
        if (this.getPrepaidReservationExpirationDelayinMillisec() != null) {
            provider.setPrepaidReservationExpirationDelayinMillisec(this.getPrepaidReservationExpirationDelayinMillisec());
        }
        if (!StringUtils.isBlank(this.getDiscountAccountingCode())) {
            provider.setDiscountAccountingCode(this.getDiscountAccountingCode());
        }
        if (!StringUtils.isBlank(this.getEmail())) {
            provider.setEmail(this.getEmail());
        }

        if (this.getRecognizeRevenue() != null) {
            provider.setRecognizeRevenue(this.getRecognizeRevenue());
        }
        if (this.getBankCoordinates() != null) {
            provider.setBankCoordinates(this.getBankCoordinates());
        }

        if (this.getPaymentPlanPolicy() != null) {
            provider.setPaymentPlanPolicy(this.getPaymentPlanPolicy());
        }        
        if (this.getMaximumDelay() != null) {
            provider.setMaximumDelay(this.getMaximumDelay());
        }
        
        if (this.getMaximumDeferralPerInvoice() != null) {
            provider.setMaximumDeferralPerInvoice(this.getMaximumDeferralPerInvoice());
        }
        if (this.getPortalMessage() != null) {
            provider.setPortalMessage(this.getPortalMessage());
        }
        if (this.getCurrentMatchingCode() != null) {
            provider.setCurrentMatchingCode(this.getCurrentMatchingCode());
        }
        
        if (this.getInvoiceConfiguration() != null) {
            InvoiceConfiguration invoiceConfiguration = new InvoiceConfiguration();
            invoiceConfiguration.setId(this.getInvoiceConfiguration().get("id"));
            provider.setInvoiceConfiguration(invoiceConfiguration);
        }
        return provider;
    }



}
