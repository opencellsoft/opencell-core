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

package org.meveo.api.dto.tunnel;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.subscriptionTunnel.ContactMethodEnum;
import org.meveo.model.subscriptionTunnel.ElectronicSignature;
import org.meveo.model.subscriptionTunnel.Theme;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The Class TunnelCustomizationDto.
 *
 * @author Ilham CHAFIK
 */
public class TunnelCustomizationDto extends BaseEntityDto {

    /** serial version uid. */
    private static final long serialVersionUID = -2250220156985183482L;

    /** The multi-language rgpd. */
    private List<LanguageDescriptionDto> rgpd;

    /** The multi-language terms and conditions. */
    private List<LanguageDescriptionDto> termsAndConditions;

    /** The multi-language order validation message. */
    private List<LanguageDescriptionDto> orderValidationMsg;

    /** The multi-language signature message. */
    private List<LanguageDescriptionDto> signatureMsg;

    /** The list of the analytics codes. */
    private Map<String, String> analytics;

    /** The list of payment methods. */
    private List<PaymentMethodEnum> paymentMethods;

    /** Is Contract feature active. */
    private Boolean isContractActive;

    /** The mandate contract (For Direct debit method). */
    private String mandateContract;

    /** the configured contact methods. */
    private List<ContactMethodEnum> contactMethods;

    /** The default billing cycle for tunnel users. */
    private BillingCycle billingCycle;

    /** The default customer category for tunnel users. */
    private CustomerCategory customerCategory;

    /** the theme applied on the tunnel. */
    private ThemeDto theme;

    /** Is electronic signature feature active. */
    private Boolean isSignatureActive;

    /** the chosen electronic signature for the tunnel. */
    private ElectronicSignatureDto electronicSignature;

    /**
     *  Instantiates the Tunnel
     */
    public TunnelCustomizationDto() {
    }

    /**
     * Instantiates the tunnel
     * @param rgpd General Data Protection Regulation
     * @param termsAndConditions terms & conditions
     * @param orderValidationMsg order validation message
     * @param signatureMsg message to ask the user to sign
     * @param analytics analytics codes
     */
    public TunnelCustomizationDto(List<LanguageDescriptionDto> rgpd,
                                  List<LanguageDescriptionDto> termsAndConditions,
                                  List<LanguageDescriptionDto> orderValidationMsg,
                                  List<LanguageDescriptionDto> signatureMsg,
                                  Map<String, String> analytics) {
        this.rgpd = rgpd;
        this.termsAndConditions = termsAndConditions;
        this.orderValidationMsg = orderValidationMsg;
        this.signatureMsg = signatureMsg;
        this.analytics = analytics;
    }

    /**
     * Instantiates the tunnel
     * @param rgpd General Data Protection Regulation
     * @param termsAndConditions terms & conditions
     * @param orderValidationMsg order validation message
     * @param signatureMsg message to ask the user to sign
     * @param analytics analytics codes
     * @param contactMethods methods to send notifications to tunnel users
     * @param theme tunnel theme
     * @param electronicSignature electronic signature params
     */
    public TunnelCustomizationDto(List<LanguageDescriptionDto> rgpd,
                                  List<LanguageDescriptionDto> termsAndConditions,
                                  List<LanguageDescriptionDto> orderValidationMsg,
                                  List<LanguageDescriptionDto> signatureMsg,
                                  Map<String, String> analytics,
                                  List<ContactMethodEnum> contactMethods,
                                  ThemeDto theme,
                                  ElectronicSignatureDto electronicSignature) {
        this.rgpd = rgpd;
        this.termsAndConditions = termsAndConditions;
        this.orderValidationMsg = orderValidationMsg;
        this.signatureMsg = signatureMsg;
        this.analytics = analytics;
        this.contactMethods = contactMethods;
        this.theme = theme;
        this.electronicSignature = electronicSignature;
    }

    /**
     * Instantiates the tunnel
     * @param rgpd General Data Protection Regulation
     * @param termsAndConditions terms & conditions
     * @param orderValidationMsg order validation message
     * @param signatureMsg message to ask the user to sign
     * @param analytics analytics codes
     * @param isContractActive is contract active
     * @param mandateContract the mandate contract
     * @param contactMethods methods to send notifications to tunnel users
     * @param billingCycle the default billing cycle
     * @param customerCategory the default customer category
     * @param theme tunnel theme
     * @param isSignatureActive is signature active
     * @param electronicSignature electronic signature params
     */
    public TunnelCustomizationDto(List<LanguageDescriptionDto> rgpd,
                                  List<LanguageDescriptionDto> termsAndConditions,
                                  List<LanguageDescriptionDto> orderValidationMsg,
                                  List<LanguageDescriptionDto> signatureMsg,
                                  Map<String, String> analytics,
                                  Boolean isContractActive,
                                  String mandateContract,
                                  List<ContactMethodEnum> contactMethods,
                                  BillingCycle billingCycle,
                                  CustomerCategory customerCategory,
                                  ThemeDto theme,
                                  Boolean isSignatureActive,
                                  ElectronicSignatureDto electronicSignature) {
        this.rgpd = rgpd;
        this.termsAndConditions = termsAndConditions;
        this.orderValidationMsg = orderValidationMsg;
        this.signatureMsg = signatureMsg;
        this.analytics = analytics;
        this.isContractActive = isContractActive;
        this.mandateContract = mandateContract;
        this.contactMethods = contactMethods;
        this.billingCycle = billingCycle;
        this.customerCategory = customerCategory;
        this.theme = theme;
        this.isSignatureActive = isSignatureActive;
        this.electronicSignature = electronicSignature;
    }

    /**
     * Gets the General Data Protection Regulation.
     * @return rgpd
     */
    public List<LanguageDescriptionDto> getRgpd() {
        return rgpd;
    }

    /**
     * Sets the General Data Protection Regulation.
     * @param rgpd General Data Protection Regulation
     */
    public void setRgpd(List<LanguageDescriptionDto> rgpd) {
        this.rgpd = rgpd;
    }

    /**
     * Gets terms and conditions.
     * @return termsAndConditions
     */
    public List<LanguageDescriptionDto> getTermsAndConditions() {
        return termsAndConditions;
    }

    /**
     * Sets terms and conditions
     * @param termsAndConditions terms and conditions
     */
    public void setTermsAndConditions(List<LanguageDescriptionDto> termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    /**
     * Gets the order validation message.
     * @return orderValidationMsg
     */
    public List<LanguageDescriptionDto> getOrderValidationMsg() {
        return orderValidationMsg;
    }

    /**
     * Sets the order validation message.
     * @param orderValidationMsg the order validation message
     */
    public void setOrderValidationMsg(List<LanguageDescriptionDto> orderValidationMsg) {
        this.orderValidationMsg = orderValidationMsg;
    }

    /**
     * Gets the signature message.
     * @return signatureMsg
     */
    public List<LanguageDescriptionDto> getSignatureMsg() {
        return signatureMsg;
    }

    /**
     * Sets the signature message.
     * @param signatureMsg the signature message
     */
    public void setSignatureMsg(List<LanguageDescriptionDto> signatureMsg) {
        this.signatureMsg = signatureMsg;
    }

    /**
     * Gets analytics.
     * @return analytics
     */
    public Map<String, String> getAnalytics() {
        return analytics;
    }

    /**
     * Sets the analytics.
     * @param analytics the analytics
     */
    public void setAnalytics(Map<String, String> analytics) {
        this.analytics = analytics;
    }

    /**
     *
     * @return paymentMethods
     */
    public List<PaymentMethodEnum> getPaymentMethods() {
        return paymentMethods;
    }

    /**
     *
     * @param paymentMethods the payment methods
     */
    public void setPaymentMethods(List<PaymentMethodEnum> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    /**
     *
     * @return isContractActive
     */
    public Boolean getContractActive() {
        return isContractActive;
    }

    /**
     *
     * @param contractActive is contract active
     */
    public void setContractActive(Boolean contractActive) {
        isContractActive = contractActive;
    }

    /**
     * Gets the mandate contract path.
     * @return the mandate contract path
     */
    public String getMandateContract() {
        return mandateContract;
    }

    /**
     * Sets the mandate contract path.
     * @param mandateContract the mandate contract path
     */
    public void setMandateContract(String mandateContract) {
        this.mandateContract = mandateContract;
    }


    /**
     * Gets the contact methods.
     * @return contactMethods
     */
    public List<ContactMethodEnum> getContactMethods() {
        return contactMethods;
    }

    /**
     * Sets the contact methods.
     * @param contactMethods the contact methods
     */
    public void setContactMethods(List<ContactMethodEnum> contactMethods) {
        this.contactMethods = contactMethods;
    }

    /**
     *
     * @return billingCycle
     */
    public BillingCycle getBillingCycle() {
        return billingCycle;
    }

    /**
     *
     * @param billingCycle the default billing cycle
     */
    public void setBillingCycle(BillingCycle billingCycle) {
        this.billingCycle = billingCycle;
    }

    /**
     *
     * @return customerCategory
     */
    public CustomerCategory getCustomerCategory() {
        return customerCategory;
    }

    /**
     *
     * @param customerCategory the customer category
     */
    public void setCustomerCategory(CustomerCategory customerCategory) {
        this.customerCategory = customerCategory;
    }

    /**
     * Gets the tunnel theme.
     * @return theme
     */
    public ThemeDto getTheme() {
        return theme;
    }

    /**
     * Sets the tunnel theme.
     * @param theme the tunnel theme
     */
    public void setTheme(ThemeDto theme) {
        this.theme = theme;
    }

    /**
     *
     * @return isSignatureActive
     */
    public Boolean getSignatureActive() {
        return isSignatureActive;
    }

    /**
     *
     * @param signatureActive is signature active
     */
    public void setSignatureActive(Boolean signatureActive) {
        isSignatureActive = signatureActive;
    }

    /**
     * Gets the electronic signature.
     * @return electronicSignature
     */
    public ElectronicSignatureDto getElectronicSignature() {
        return electronicSignature;
    }

    /**
     * Sets the electronic signature.
     * @param electronicSignature the electronic signature
     */
    public void setElectronicSignature(ElectronicSignatureDto electronicSignature) {
        this.electronicSignature = electronicSignature;
    }
}
