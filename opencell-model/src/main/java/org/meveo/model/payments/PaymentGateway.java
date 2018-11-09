/**
 * 
 */
package org.meveo.model.payments;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableBusinessCFEntity;
import org.meveo.model.ModuleItem;
import org.meveo.model.billing.Country;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.scripts.ScriptInstance;

/**
 * The PaymentGateway on opencell exists in 2 types {@link org.meveo.model.payments.PaymentGatewayTypeEnum PaymentGatewayTypeEnum}: &lt;ul&gt; &lt;li&gt;Custom: The administrator
 * can define the implementation in a script.&lt;/li&gt; &lt;li&gt;Native: The business implementation code is available on the opencell core, currently the available PSP are
 * Inginico Ogone, and Slimpay .&lt;/li&gt; &lt;/ul&gt;
 *
 *
 * @author anasseh
 * @since Opencell 4.8
 * @author Mounir Bahije
 * @lastModifiedVersion 5.2
 */

@Entity
@ModuleItem
@CustomFieldEntity(cftCodePrefix = "PAYMENT_GW")
@Table(name = "ar_payment_gateway", uniqueConstraints = @UniqueConstraint(columnNames = { "payment_method", "country_id", "trading_currency_id" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_payment_gateway_seq"), })
public class PaymentGateway extends EnableBusinessCFEntity {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 697688141736383814L;

    /** The type. */
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentGatewayTypeEnum type;

    /** Payment method allowed on the payment gateway. */
    @Column(name = "payment_method", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethodEnum paymentMethodType;

    /** The script instance. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "script_instance_id")
    private ScriptInstance scriptInstance;

    /** The implementation class name. */
    @Column(name = "implementation_class_name", length = 255)
    @Size(max = 255)
    private String implementationClassName;

    /** The application EL. */
    @Column(name = "application_el", length = 2000)
    @Size(max = 2000)
    private String applicationEL;

    /** The trading country. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    /** The trading currency. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_currency_id")
    private TradingCurrency tradingCurrency;

    /** The card type. */
    @Enumerated(EnumType.STRING)
    @Column(name = "card_type")
    private CreditCardTypeEnum cardType;

    /** The nb tries. */
    @Column(name = "nb_tries")
    private Integer nbTries;

    /** The replay cause. */
    @Enumerated(EnumType.STRING)
    @Column(name = "replay_cause")
    private PaymentReplayCauseEnum replayCause;

    /** The errors to replay. */
    @Column(name = "errors_to_replay")
    private String errorsToReplay;
    
    /** The marchand id. */
    @Column(name = "marchand_id")
    private String marchandId;
    
    /** The secret key. */
    @Column(name = "secret_key")
    private String secretKey;
    
    /** The api key. */
    @Column(name = "api_key")
    private String apiKey;

    /** The webhooks key id. */
    @Column(name = "webhooks_key_id")
    private String webhooksKeyId;

    /** The webhooks secret key. */
    @Column(name = "webhooks_secret_key")
    private String webhooksSecretKey;

    /** The profile. */
    @Column(name = "profile")
    private String profile;
    
    /**
     * Instantiates a new payment gateway.
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
    public String getSecretKey() {
        return secretKey;
    }

    /**
     * Sets the secret key.
     *
     * @param secretKey the secretKey to set
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
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

    /* (non-Javadoc)
     * @see org.meveo.model.BusinessEntity#toString()
     */
    @Override
    public String toString() {
        return "PaymentGateway [marchandId="+marchandId+", type=" + type + ", paymentMethodType=" + paymentMethodType + ", scriptInstance=" + (scriptInstance == null ? null : scriptInstance.getCode())
                + ", implementationClassName=" + implementationClassName + ", applicationEL=" + applicationEL + ", Country=" + (country == null ? null : country.getCountryCode())
                + ", tradingCurrency=" + (tradingCurrency == null ? null : tradingCurrency.getCurrencyCode()) + ", cardType=" + cardType + "]";
    }
}
