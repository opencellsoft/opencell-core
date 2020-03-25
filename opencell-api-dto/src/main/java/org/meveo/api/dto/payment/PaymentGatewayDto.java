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

/**
 * 
 */
package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.api.dto.account.BankCoordinatesDto;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentGatewayTypeEnum;
import org.meveo.model.payments.PaymentMethodEnum;

/**
 * The PaymentGateway Dto.
 * 
 * @author anasseh
 * @author Mounir Bahije
 * @lastModifiedVersion 5.2
 */
@XmlRootElement(name = "PaymentGateway")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentGatewayDto extends EnableBusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8975150158860312801L;

    /** The type. */
    private PaymentGatewayTypeEnum type;

    /** The payment method. */
    private PaymentMethodEnum paymentMethodType;

    /** The script instance code. */
    private String scriptInstanceCode;

    /** The implementation class name. */
    private String implementationClassName;

    /** The application EL. */
    private String applicationEL;

    /** The country code. */
    private String countryCode;

    /** The trading currency code. */
    private String tradingCurrencyCode;

    /** The card type. */
    private CreditCardTypeEnum cardType;

    /** The marchand id. */
    private String marchandId;

    /** The secret key. */
    private String secretKey;

    /** The api key. */
    private String apiKey;

    /** The webhooks secret key. */
    private String webhooksSecretKey;

    /** The webhooks key id. */
    private String webhooksKeyId;

    /** The profile. */
    private String profile;

    /** The custom fields. */
    private CustomFieldsDto customFields;

    /**
     * The RUM sequence associated to this PaymentGateway.
     */
    private PaymentGatewayRumSequenceDto rumSequence;

    /** The seller code. */
    private String sellerCode;

    /** The bank coordinates. */
    private BankCoordinatesDto bankCoordinates = new BankCoordinatesDto();

    /**
     * Instantiates a new payment gateway dto.
     */
    public PaymentGatewayDto() {

    }

    /**
     * Convert payment gateway entity to DTO
     * 
     * @param paymentGateway Entity to convert
     */
    public PaymentGatewayDto(PaymentGateway paymentGateway) {

        super(paymentGateway);

        this.id = paymentGateway.getId();
        this.applicationEL = paymentGateway.getApplicationEL();
        this.cardType = paymentGateway.getCardType();
        this.implementationClassName = paymentGateway.getImplementationClassName();
        this.paymentMethodType = paymentGateway.getPaymentMethodType();
        this.scriptInstanceCode = paymentGateway.getScriptInstance() == null ? null : paymentGateway.getScriptInstance().getCode();
        this.countryCode = paymentGateway.getCountry() == null ? null : paymentGateway.getCountry().getCountryCode();
        this.tradingCurrencyCode = paymentGateway.getTradingCurrency() == null ? null : paymentGateway.getTradingCurrency().getCurrencyCode();
        this.type = paymentGateway.getType();
        this.marchandId = paymentGateway.getMarchandId();
        this.secretKey = "*******";
        this.apiKey = paymentGateway.getApiKey();
        this.webhooksKeyId = paymentGateway.getWebhooksKeyId();
        this.webhooksSecretKey = paymentGateway.getWebhooksSecretKey();
        this.profile = paymentGateway.getProfile();
        if (paymentGateway.getRumSequence() != null) {
            rumSequence = new PaymentGatewayRumSequenceDto(paymentGateway.getRumSequence());
        }
        this.sellerCode = paymentGateway.getSeller() == null ? null : paymentGateway.getSeller().getCode();
        if (paymentGateway.getBankCoordinates() != null) {
            this.setBankCoordinates(new BankCoordinatesDto(paymentGateway.getBankCoordinates()));
        }
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public PaymentGatewayTypeEnum getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the type to set
     */
    public void setType(PaymentGatewayTypeEnum type) {
        this.type = type;
    }

    /**
     * Gets the payment method type.
     *
     * @return the paymentMethodType
     */
    public PaymentMethodEnum getPaymentMethodType() {
        return paymentMethodType;
    }

    /**
     * Sets the payment method type.
     *
     * @param paymentMethodType the paymentMethodType to set
     */
    public void setPaymentMethodType(PaymentMethodEnum paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
    }

    /**
     * Gets the script instance code.
     *
     * @return the scriptInstanceCode
     */
    public String getScriptInstanceCode() {
        return scriptInstanceCode;
    }

    /**
     * Sets the script instance code.
     *
     * @param scriptInstanceCode the scriptInstanceCode to set
     */
    public void setScriptInstanceCode(String scriptInstanceCode) {
        this.scriptInstanceCode = scriptInstanceCode;
    }

    /**
     * Gets the implementation class name.
     *
     * @return the implementationClassName
     */
    public String getImplementationClassName() {
        return implementationClassName;
    }

    /**
     * Sets the implementation class name.
     *
     * @param implementationClassName the implementationClassName to set
     */
    public void setImplementationClassName(String implementationClassName) {
        this.implementationClassName = implementationClassName;
    }

    /**
     * Gets the application EL.
     *
     * @return the applicationEL
     */
    public String getApplicationEL() {
        return applicationEL;
    }

    /**
     * Sets the application EL.
     *
     * @param applicationEL the applicationEL to set
     */
    public void setApplicationEL(String applicationEL) {
        this.applicationEL = applicationEL;
    }

    /**
     * Gets the card type.
     *
     * @return the cardType
     */
    public CreditCardTypeEnum getCardType() {
        return cardType;
    }

    /**
     * Sets the card type.
     *
     * @param cardType the cardType to set
     */
    public void setCardType(CreditCardTypeEnum cardType) {
        this.cardType = cardType;
    }

    /**
     * Gets the country code.
     *
     * @return the countryCode
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Sets the country code.
     *
     * @param countryCode the countryCode to set
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * Gets the trading currency code.
     *
     * @return the tradingCurrencyCode
     */
    public String getTradingCurrencyCode() {
        return tradingCurrencyCode;
    }

    /**
     * Sets the trading currency code.
     *
     * @param tradingCurrencyCode the tradingCurrencyCode to set
     */
    public void setTradingCurrencyCode(String tradingCurrencyCode) {
        this.tradingCurrencyCode = tradingCurrencyCode;
    }

    /**
     * Gets the custom fields.
     *
     * @return the customFields
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * Sets the custom fields.
     *
     * @param customFields the customFields to set
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    /**
     * @return the marchandId
     */
    public String getMarchandId() {
        return marchandId;
    }

    /**
     * @param marchandId the marchandId to set
     */
    public void setMarchandId(String marchandId) {
        this.marchandId = marchandId;
    }

    /**
     * @return the secretKey
     */
    public String getSecretKey() {
        return secretKey;
    }

    /**
     * @param secretKey the secretKey to set
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * @return the apiKey
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * @param apiKey the apiKey to set
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Gets Webhooks Secret Key
     * 
     * @return
     */
    public String getWebhooksSecretKey() {
        return webhooksSecretKey;
    }

    /**
     * Sets Webhooks Secret Key
     * 
     * @param webhooksSecretKey
     */
    public void setWebhooksSecretKey(String webhooksSecretKey) {
        this.webhooksSecretKey = webhooksSecretKey;
    }

    /**
     * Gets Webhooks Key Id
     * 
     * @return
     */
    public String getWebhooksKeyId() {
        return webhooksKeyId;
    }

    /**
     * Sets webhooks Key Id
     * 
     * @param webhooksKeyId
     */
    public void setWebhooksKeyId(String webhooksKeyId) {
        this.webhooksKeyId = webhooksKeyId;
    }

    /**
     * @return the profile
     */
    public String getProfile() {
        return profile;
    }

    /**
     * @param profile the profile to set
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "PaymentGatewayDto [id=" + id + ", code=" + code + ", description=" + description + ", updatedCode=" + updatedCode + ", type=" + type + ", paymentMethodType="
                + paymentMethodType + ", scriptInstanceCode=" + scriptInstanceCode + ", implementationClassName=" + implementationClassName + ", applicationEL=" + applicationEL
                + ", countryCode=" + countryCode + ", tradingCurrencyCode=" + tradingCurrencyCode + ", cardType=" + cardType + ", marchandId=" + marchandId + ", sellerCode="
                + sellerCode + "]";
    }

    public PaymentGatewayRumSequenceDto getRumSequence() {
        return rumSequence;
    }

    public void setRumSequence(PaymentGatewayRumSequenceDto rumSequence) {
        this.rumSequence = rumSequence;
    }

    /**
     * @return the sellerCode
     */
    public String getSellerCode() {
        return sellerCode;
    }

    /**
     * @param sellerCode the sellerCode to set
     */
    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
    }

    /**
     * @return the bankCoordinates
     */
    public BankCoordinatesDto getBankCoordinates() {
        return bankCoordinates;
    }

    /**
     * @param bankCoordinates the bankCoordinates to set
     */
    public void setBankCoordinates(BankCoordinatesDto bankCoordinates) {
        this.bankCoordinates = bankCoordinates;
    }

}
