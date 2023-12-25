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
package org.meveo.model.payments;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostPersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.commons.keystore.KeystoreManager;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableBusinessCFEntity;
import org.meveo.model.ISearchable;
import org.meveo.model.ModuleItem;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BankCoordinates;
import org.meveo.model.billing.Country;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.scripts.ScriptInstance;

import java.util.List;

/**
 * The PaymentGateway on opencell exists in 2 types {@link org.meveo.model.payments.PaymentGatewayTypeEnum PaymentGatewayTypeEnum}: &lt;ul&gt; &lt;li&gt;Custom: The administrator
 * can define the implementation in a script.&lt;/li&gt; &lt;li&gt;Native: The business implementation code is available on the opencell core, currently the available PSP are
 * Inginico Ogone, and Slimpay .&lt;/li&gt; &lt;/ul&gt;
 *
 *
 * @author anasseh
 * @author Mounir Bahije
 * @author Abdellatif BARI
 * @since Opencell 4.8
 * @lastModifiedVersion 7.0
 */

@Entity
@ModuleItem
@CustomFieldEntity(cftCodePrefix = "PaymentGateway")
@Table(name = "ar_payment_gateway", uniqueConstraints = @UniqueConstraint(columnNames = { "payment_method", "country_id", "trading_currency_id" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_payment_gateway_seq"), })
public class PaymentGateway extends EnableBusinessCFEntity implements ISearchable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 697688141736383814L;

    /**
     * The type
     */
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentGatewayTypeEnum type;

    /**
     * Payment method allowed on the payment gateway
     */
    @Column(name = "payment_method", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethodEnum paymentMethodType;

    /**
     * The script instance
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "script_instance_id")
    private ScriptInstance scriptInstance;

    /**
     * The implementation class name
     */
    @Column(name = "implementation_class_name", length = 255)
    @Size(max = 255)
    private String implementationClassName;

    /**
     * The application EL
     */
    @Column(name = "application_el", length = 2000)
    @Size(max = 2000)
    private String applicationEL;

    /**
     * The trading country
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    /**
     * The trading currency
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_currency_id")
    private TradingCurrency tradingCurrency;

    /**
     * The card type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "card_type")
    private CreditCardTypeEnum cardType;

    /**
     * The nb tries
     */
    @Column(name = "nb_tries")
    private Integer nbTries;

    /**
     * The replay cause
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "replay_cause")
    private PaymentReplayCauseEnum replayCause;

    /**
     * The errors to replay
     */
    @Column(name = "errors_to_replay")
    private String errorsToReplay;

    /**
     * The marchand id
     */
    @Column(name = "marchand_id")
    private String marchandId;

    /**
     * The secret key
     */
    @Column(name = "secret_key")
    private String secretKeyDB;

    /**
     * transient secretKey
     */
    transient private String secretKey;

    /**
     * transient secretKey in Keystore
     */
    transient private String secretKeyKS;

    /**
     * The api key
     */
    @Column(name = "api_key")
    private String apiKey;

    /** The webhooks key id. */
    @Column(name = "webhooks_key_id")
    private String webhooksKeyId;

    /** The webhooks secret key. */
    @Column(name = "webhooks_secret_key")
    private String webhooksSecretKey;

    /**
     * The profile
     */
    @Column(name = "profile")
    private String profile;
    
	@OneToOne(mappedBy = "paymentGateway", cascade = CascadeType.ALL, orphanRemoval = true)
    private PaymentGatewayRumSequence rumSequence;

	
    /**
     * Bank coordinates
     */
    @Embedded
    private BankCoordinates bankCoordinates = new BankCoordinates();
    

    /**
     * Seller associated to a customer
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;

    /**
     * Rejection code associated to payment gateway
     */
    @OneToMany(mappedBy = "paymentGateway", fetch = FetchType.LAZY)
    private List<PaymentRejectionCode> paymentRejectionCodes;
    
    /**
     * Instantiates a new payment gateway
     */
    public PaymentGateway() {

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
     * Gets the script instance.
     *
     * @return the scriptInstance
     */
    public ScriptInstance getScriptInstance() {
        return scriptInstance;
    }

    /**
     * Sets the script instance.
     *
     * @param scriptInstance the scriptInstance to set
     */
    public void setScriptInstance(ScriptInstance scriptInstance) {
        this.scriptInstance = scriptInstance;
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
     * Gets the country.
     *
     * @return the country
     */
    public Country getCountry() {
        return country;
    }

    /**
     * Sets the country.
     *
     * @param country the country to set
     */
    public void setCountry(Country country) {
        this.country = country;
    }

    /**
     * Gets the trading currency.
     *
     * @return the tradingCurrency
     */
    public TradingCurrency getTradingCurrency() {
        return tradingCurrency;
    }

    /**
     * Sets the trading currency.
     *
     * @param tradingCurrency the tradingCurrency to set
     */
    public void setTradingCurrency(TradingCurrency tradingCurrency) {
        this.tradingCurrency = tradingCurrency;
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
     * Gets the nb tries.
     *
     * @return the nbTries
     */
    public Integer getNbTries() {
        return nbTries;
    }

    /**
     * Sets the nb tries.
     *
     * @param nbTries the nbTries to set
     */
    public void setNbTries(Integer nbTries) {
        this.nbTries = nbTries;
    }

    /**
     * Gets the replay cause.
     *
     * @return the replayCause
     */
    public PaymentReplayCauseEnum getReplayCause() {
        return replayCause;
    }

    /**
     * Sets the replay cause.
     *
     * @param replayCause the replayCause to set
     */
    public void setReplayCause(PaymentReplayCauseEnum replayCause) {
        this.replayCause = replayCause;
    }

    /**
     * Gets the errors to replay.
     *
     * @return the errorsToReplay
     */
    public String getErrorsToReplay() {
        return errorsToReplay;
    }

    /**
     * Sets the errors to replay.
     *
     * @param errorsToReplay the errorsToReplay to set
     */
    public void setErrorsToReplay(String errorsToReplay) {
        this.errorsToReplay = errorsToReplay;
    }

    /**
     * Gets the marchand id.
     *
     * @return the marchandId
     */
    public String getMarchandId() {
        return marchandId;
    }

    /**
     * Sets the marchand id.
     *
     * @param marchandId the marchandId to set
     */
    public void setMarchandId(String marchandId) {
        this.marchandId = marchandId;
    }

    /**
     * Gets the secret key.
     *
     * @return the secretKey
     */
    public String getSecretKeyDB() {
        return secretKeyDB;
    }

    /**
     * Sets the secret key.
     *
     * @param secretKeyDB the secretKey to set
     */
    public void setSecretKeyDB(String secretKeyDB) {
        this.secretKeyDB = secretKeyDB;
    }

    public String getSecretKey() {
        if (KeystoreManager.existKeystore()) {
            return getSecretKeyKS();
        }
        else {
            return getSecretKeyDB();
        }
    }

    public void setSecretKey(String password) {
        if (KeystoreManager.existKeystore()) {
            secretKeyDB = "";
            this.secretKeyKS = password;
            setSecretKeyKS();
        }
        else {
            setSecretKeyDB(password);
        }
    }

    public String getSecretKeyKS() {
        if (KeystoreManager.existCredential(getClass().getSimpleName() + "." + getId())) {
            return KeystoreManager.retrieveCredential(getClass().getSimpleName() + "." + getId());
        }
        else {
            return "";
        }
    }

    @PostPersist
    public void setSecretKeyKS() {
        if (this.secretKeyKS == null) {
            this.secretKeyKS = "";
        }

        if (getId() != null && KeystoreManager.existKeystore() &&! this.secretKeyKS.equals(getSecretKeyKS())) {
            KeystoreManager.addCredential(getClass().getSimpleName() + "." + getId(), this.secretKeyKS);
        }
    }

    /**
     * Gets the api key.
     *
     * @return the apiKey
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * Sets the api key.
     *
     * @param apiKey the apiKey to set
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
      * Gets  Webhooks Key Id
      * @return the webhooksKeyId
      */
    public String getWebhooksKeyId() {
        return webhooksKeyId;
    }

    /**
      * Sets the Webhooks Key Id.
      * @param webhooksKeyId
      */
    public void setWebhooksKeyId(String webhooksKeyId) {
        this.webhooksKeyId = webhooksKeyId;
    }

    /**
     * Gets the Webhooks Secret Key.
     * @return the webhooksSecretKey
     */
    public String getWebhooksSecretKey() {
        return webhooksSecretKey;
    }

    /**
     * Sets the Webhooks Secret Key.
     * @param webhooksSecretKey
     */
    public void setWebhooksSecretKey(String webhooksSecretKey) {
        this.webhooksSecretKey = webhooksSecretKey;
    }

    /**
     * Gets the profile.
     *
     * @return the profile
     */
    public String getProfile() {
        return profile;
    }

    /**
     * Sets the profile.
     *
     * @param profile the profile to set
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }


    @Override
    public String toString() {
        return "PaymentGateway [marchandId=" + marchandId + ", type=" + type + ", paymentMethodType=" + paymentMethodType + ", scriptInstance="
                + (scriptInstance == null ? null : scriptInstance.getCode()) + ", implementationClassName=" + implementationClassName + ", applicationEL=" + applicationEL
                + ", Country=" + (country == null ? null : country.getCountryCode()) + ", tradingCurrency=" + (tradingCurrency == null ? null : tradingCurrency.getCurrencyCode())
                + ", cardType=" + cardType + "]";
    }

	public PaymentGatewayRumSequence getRumSequence() {
		return rumSequence;
	}

	public void setRumSequence(PaymentGatewayRumSequence rumSequence) {
		this.rumSequence = rumSequence;
	}

    /**
     * @return the bankCoordinates
     */
    public BankCoordinates getBankCoordinates() {
        return bankCoordinates;
    }

    /**
     * @param bankCoordinates the bankCoordinates to set
     */
    public void setBankCoordinates(BankCoordinates bankCoordinates) {
        this.bankCoordinates = bankCoordinates;
    }

    /**
     * @return the seller
     */
    public Seller getSeller() {
        return seller;
    }

    /**
     * @param seller the seller to set
     */
    public void setSeller(Seller seller) {
        this.seller = seller;
    }


    public List<PaymentRejectionCode> getPaymentRejectionCodes() {
        return paymentRejectionCodes;
    }

    public void setPaymentRejectionCodes(List<PaymentRejectionCode> paymentRejectionCodes) {
        this.paymentRejectionCodes = paymentRejectionCodes;
    }
}
