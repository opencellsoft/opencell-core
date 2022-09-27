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

package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.BankCoordinatesDto;
import org.meveo.api.dto.response.payment.PaymentPlanPolicyDto;
import org.meveo.api.dto.invoice.InvoiceConfigurationDto;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.crm.Provider;

/**
 * The Class ProviderDto.
 *
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "Provider")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProviderDto extends AuditableEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5599223889050605880L;

    /**
     * The code.
     */
    @XmlAttribute(required = true)
    private String code;

    /**
     * The description.
     */
    private String description;

    /**
     * The currency.
     */
    private String currency;

    /**
     * The country.
     */
    private String country;

    /**
     * The language.
     */
    private String language;

    /**
     * The multi currency.
     */
    private Boolean multiCurrency;

    /**
     * The multi country.
     */
    private Boolean multiCountry;

    /**
     * The multi language.
     */
    private Boolean multiLanguage;

    /**
     * The user account.
     */
    private String userAccount;

    /**
     * The enterprise.
     */
    private Boolean enterprise;

    /**
     * The level duplication.
     */
    private Boolean levelDuplication;

    /**
     * The rounding.
     */
    private Integer rounding;

    /**
     * The Rating amount rounding mode
     */
    private RoundingModeEnum roundingMode;

    /**
     * The invoice amount rounding.
     */
    private Integer invoiceRounding;

    /**
     * The invoice amount rounding mode.
     */
    private RoundingModeEnum invoiceRoundingMode;

    /**
     * The prepaid reservation expiration delayin millisec.
     */
    private Long prepaidReservationExpirationDelayinMillisec;

    /**
     * The discount accounting code.
     *
     * @deprecated Not used.
     */
    @Deprecated
    private String discountAccountingCode;

    /** The email. */
    private String email;

    /** The bank coordinates. */
    private BankCoordinatesDto bankCoordinates = new BankCoordinatesDto();
    
    /** The Payment Plan Policy Dto. */
    private PaymentPlanPolicyDto paymentPlanPolicy = new PaymentPlanPolicyDto();

	/** The recognize revenue. */
    private Boolean recognizeRevenue;

    /** The invoice configuration. */
    private InvoiceConfigurationDto invoiceConfiguration;

    /** The custom fields. */
    @XmlElement(required = false)
    private CustomFieldsDto customFields;
    
    /** if set to expression, will create cdr.originRecord dynamically**/
    private String cdrDeduplicationKeyEL;

    /**
     * Instantiates a new provider dto.
     */
    public ProviderDto() {
    }

    /**
     * Instantiates a new provider dto.
     *
     * @param provider the provider entity
     * @param customFieldInstances the custom field instances
     */
    public ProviderDto(Provider provider, CustomFieldsDto customFieldInstances) {
        this(provider, customFieldInstances, true);
    }

    /**
     * Instantiates a new provider dto.
     *
     * @param provider the provider
     * @param customFieldInstances the custom field instances
     * @param loadProviderData the load provider data
     */
    public ProviderDto(Provider provider, CustomFieldsDto customFieldInstances, boolean loadProviderData) {
        super(provider);
        code = provider.getCode();

        if (loadProviderData) {
            description = provider.getDescription();
            if (provider.getCurrency() != null) {
                currency = provider.getCurrency().getCurrencyCode();
            }
            if (provider.getCountry() != null) {
                country = provider.getCountry().getCountryCode();
            }
            if (provider.getLanguage() != null) {
                language = provider.getLanguage().getLanguageCode();
            }
            multiCurrency = provider.getMulticurrencyFlag();
            multiCountry = provider.getMulticountryFlag();
            multiLanguage = provider.getMultilanguageFlag();
            rounding = provider.getRounding();
            roundingMode = provider.getRoundingMode();
            invoiceRounding = provider.getInvoiceRounding();
            invoiceRoundingMode = provider.getInvoiceRoundingMode();
            prepaidReservationExpirationDelayinMillisec = provider.getPrepaidReservationExpirationDelayinMillisec();
            discountAccountingCode = provider.getDiscountAccountingCode();
            email = provider.getEmail();
            cdrDeduplicationKeyEL=provider.getCdrDeduplicationKeyEL();

            this.setEnterprise(provider.isEntreprise());
            this.setLevelDuplication(provider.isLevelDuplication());

            this.setRecognizeRevenue(provider.isRecognizeRevenue());

            if (provider.getBankCoordinates() != null) {
                this.setBankCoordinates(new BankCoordinatesDto(provider.getBankCoordinates()));
            }            

            if (provider.getPaymentPlanPolicy() != null) {
                this.setPaymentPlanPolicy(new PaymentPlanPolicyDto(provider.getPaymentPlanPolicy()));
            }
            if (provider.getInvoiceConfiguration() != null) {
                this.setInvoiceConfiguration(new InvoiceConfigurationDto(provider.getInvoiceConfiguration()));
            } else {
                this.setInvoiceConfiguration(new InvoiceConfigurationDto());
            }
            this.getInvoiceConfiguration().setDisplayFreeTransacInInvoice(provider.isDisplayFreeTransacInInvoice());
        }

        customFields = customFieldInstances;
    }

    /**
     * Gets the code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     *
     * @param code the new code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the currency.
     *
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the currency.
     *
     * @param currency the new currency
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Gets the country.
     *
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country.
     *
     * @param country the new country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets the language.
     *
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the language.
     *
     * @param language the new language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Checks if is multi currency.
     *
     * @return the boolean
     */
    public Boolean isMultiCurrency() {
        return multiCurrency;
    }

    /**
     * Sets the multi currency.
     *
     * @param multiCurrency the new multi currency
     */
    public void setMultiCurrency(Boolean multiCurrency) {
        this.multiCurrency = multiCurrency;
    }

    /**
     * Checks if is multi country.
     *
     * @return the boolean
     */
    public Boolean isMultiCountry() {
        return multiCountry;
    }

    /**
     * Sets the multi country.
     *
     * @param multiCountry the new multi country
     */
    public void setMultiCountry(Boolean multiCountry) {
        this.multiCountry = multiCountry;
    }

    /**
     * Checks if is multi language.
     *
     * @return the boolean
     */
    public Boolean isMultiLanguage() {
        return multiLanguage;
    }

    /**
     * Sets the multi language.
     *
     * @param multiLanguage the new multi language
     */
    public void setMultiLanguage(Boolean multiLanguage) {
        this.multiLanguage = multiLanguage;
    }

    /**
     * Gets the user account.
     *
     * @return the user account
     */
    public String getUserAccount() {
        return userAccount;
    }

    /**
     * Sets the user account.
     *
     * @param userAccount the new user account
     */
    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    /**
     * Gets the custom fields.
     *
     * @return the custom fields
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * Sets the custom fields.
     *
     * @param customFields the new custom fields
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    /**
     * Checks if is enterprise.
     *
     * @return the boolean
     */
    public Boolean isEnterprise() {
        return enterprise;
    }

    /**
     * Sets the enterprise.
     *
     * @param enterprise the new enterprise
     */
    public void setEnterprise(Boolean enterprise) {
        this.enterprise = enterprise;
    }

    /**
     * Checks if is level duplication.
     *
     * @return the boolean
     */
    public Boolean isLevelDuplication() {
        return levelDuplication;
    }

    /**
     * Sets the level duplication.
     *
     * @param levelDuplication the new level duplication
     */
    public void setLevelDuplication(Boolean levelDuplication) {
        this.levelDuplication = levelDuplication;
    }

    /**
     * Gets the bank coordinates.
     *
     * @return the bank coordinates
     */
    public BankCoordinatesDto getBankCoordinates() {
        return bankCoordinates;
    }

    /**
     * Sets the bank coordinates.
     *
     * @param bankCoordinates the new bank coordinates
     */
    public void setBankCoordinates(BankCoordinatesDto bankCoordinates) {
        this.bankCoordinates = bankCoordinates;
    }

    /**
     * Gets the invoice configuration.
     *
     * @return the invoice configuration
     */
    public InvoiceConfigurationDto getInvoiceConfiguration() {
        return invoiceConfiguration;
    }

    /**
     * Sets the invoice configuration.
     *
     * @param invoiceConfiguration the new invoice configuration
     */
    public void setInvoiceConfiguration(InvoiceConfigurationDto invoiceConfiguration) {
        this.invoiceConfiguration = invoiceConfiguration;
    }

    /**
     * @return Rating amount rounding precision
     */
    public Integer getRounding() {
        return rounding;
    }

    /**
     * @param rounding Rating amount rounding precision
     */
    public void setRounding(Integer rounding) {
        this.rounding = rounding;
    }

    /**
     * @return Rating amount rounding mode
     */
    public RoundingModeEnum getRoundingMode() {
        return roundingMode;
    }

    /**
     * @param roundingMode Rating amount rounding mode
     */
    public void setRoundingMode(RoundingModeEnum roundingMode) {
        this.roundingMode = roundingMode;
    }

    /**
     * @return Invoice and invoice aggregate amount rounding precision
     */
    public Integer getInvoiceRounding() {
        return invoiceRounding;
    }

    /**
     * @param invoiceRounding Invoice and invoice aggregate amount rounding precision
     */
    public void setInvoiceRounding(Integer invoiceRounding) {
        this.invoiceRounding = invoiceRounding;
    }

    /**
     * @return Invoice and invoice aggregate amount rounding mode
     */
    public RoundingModeEnum getInvoiceRoundingMode() {
        return invoiceRoundingMode;
    }

    /**
     * @param invoiceRoundingMode Invoice and invoice aggregate amount rounding mode
     */
    public void setInvoiceRoundingMode(RoundingModeEnum invoiceRoundingMode) {
        this.invoiceRoundingMode = invoiceRoundingMode;
    }

    /**
     * Gets the prepaid reservation expiration delayin millisec.
     *
     * @return the prepaid reservation expiration delayin millisec
     */
    public Long getPrepaidReservationExpirationDelayinMillisec() {
        return prepaidReservationExpirationDelayinMillisec;
    }

    /**
     * Sets the prepaid reservation expiration delayin millisec.
     *
     * @param prepaidReservationExpirationDelayinMillisec the new prepaid reservation expiration delayin millisec
     */
    public void setPrepaidReservationExpirationDelayinMillisec(Long prepaidReservationExpirationDelayinMillisec) {
        this.prepaidReservationExpirationDelayinMillisec = prepaidReservationExpirationDelayinMillisec;
    }

    /**
     * Gets the discount accounting code.
     *
     * @return the discount accounting code
     */
    public String getDiscountAccountingCode() {
        return discountAccountingCode;
    }

    /**
     * Sets the discount accounting code.
     *
     * @param discountAccountingCode the new discount accounting code
     */
    public void setDiscountAccountingCode(String discountAccountingCode) {
        this.discountAccountingCode = discountAccountingCode;
    }

    /**
     * Gets the email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email.
     *
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Checks if is recognize revenue.
     *
     * @return the boolean
     */
    public Boolean isRecognizeRevenue() {
        return recognizeRevenue;
    }

    /**
     * Sets the recognize revenue.
     *
     * @param recognizeRevenue the new recognize revenue
     */
    public void setRecognizeRevenue(Boolean recognizeRevenue) {
        this.recognizeRevenue = recognizeRevenue;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ProviderDto [code=" + code + ", description=" + description + ", currency=" + currency + ", country=" + country + ", language=" + language + ", multiCurrency=" + multiCurrency + ", multiCountry="
                + multiCountry + ", multiLanguage=" + multiLanguage + ", userAccount=" + userAccount + ", enterprise=" + enterprise + ", levelDuplication=" + levelDuplication + ", rounding=" + rounding
                + ", prepaidReservationExpirationDelayinMillisec=" + prepaidReservationExpirationDelayinMillisec + ", discountAccountingCode=" + discountAccountingCode + ", email=" + email + ", bankCoordinates="
                + bankCoordinates + ", recognizeRevenue=" + recognizeRevenue + ", invoiceConfiguration=" + invoiceConfiguration + ", customFields=" + customFields + "]";
    }

	/**
	 * @return the cdrDeduplicationKeyEL
	 */
	public String getCdrDeduplicationKeyEL() {
		return cdrDeduplicationKeyEL;
	}

	/**
	 * @param cdrDeduplicationKeyEL the cdrDeduplicationKeyEL to set
	 */
	public void setCdrDeduplicationKeyEL(String cdrDeduplicationKeyEL) {
		this.cdrDeduplicationKeyEL = cdrDeduplicationKeyEL;
	}

	public PaymentPlanPolicyDto getPaymentPlanPolicy() {
		return paymentPlanPolicy;
	}

	public void setPaymentPlanPolicy(PaymentPlanPolicyDto paymentPlanPolicy) {
		this.paymentPlanPolicy = paymentPlanPolicy;
	}

}
