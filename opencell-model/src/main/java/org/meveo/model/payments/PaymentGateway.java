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
 * @lastModifiedVersion 5.0.1
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

    @Column(name = "errors_to_replay")
    private String errorsToReplay;

    public PaymentGateway() {

    }

    /**
     * @return the type
     */
    public PaymentGatewayTypeEnum getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(PaymentGatewayTypeEnum type) {
        this.type = type;
    }

    /**
     * @return the scriptInstance
     */
    public ScriptInstance getScriptInstance() {
        return scriptInstance;
    }

    /**
     * @param scriptInstance the scriptInstance to set
     */
    public void setScriptInstance(ScriptInstance scriptInstance) {
        this.scriptInstance = scriptInstance;
    }

    /**
     * @return the implementationClassName
     */
    public String getImplementationClassName() {
        return implementationClassName;
    }

    /**
     * @param implementationClassName the implementationClassName to set
     */
    public void setImplementationClassName(String implementationClassName) {
        this.implementationClassName = implementationClassName;
    }

    /**
     * @return the applicationEL
     */
    public String getApplicationEL() {
        return applicationEL;
    }

    /**
     * @param applicationEL the applicationEL to set
     */
    public void setApplicationEL(String applicationEL) {
        this.applicationEL = applicationEL;
    }

    /**
     * @return the country
     */
    public Country getCountry() {
        return country;
    }

    /**
     * @param country the country to set
     */
    public void setCountry(Country country) {
        this.country = country;
    }

    /**
     * @return the tradingCurrency
     */
    public TradingCurrency getTradingCurrency() {
        return tradingCurrency;
    }

    /**
     * @param tradingCurrency the tradingCurrency to set
     */
    public void setTradingCurrency(TradingCurrency tradingCurrency) {
        this.tradingCurrency = tradingCurrency;
    }

    /**
     * @return the cardType
     */
    public CreditCardTypeEnum getCardType() {
        return cardType;
    }

    /**
     * @param cardType the cardType to set
     */
    public void setCardType(CreditCardTypeEnum cardType) {
        this.cardType = cardType;
    }

    /**
     * @return the paymentMethodType
     */
    public PaymentMethodEnum getPaymentMethodType() {
        return paymentMethodType;
    }

    /**
     * @param paymentMethodType the paymentMethodType to set
     */
    public void setPaymentMethodType(PaymentMethodEnum paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
    }

    /**
     * @return the nbTries
     */
    public Integer getNbTries() {
        return nbTries;
    }

    /**
     * @param nbTries the nbTries to set
     */
    public void setNbTries(Integer nbTries) {
        this.nbTries = nbTries;
    }

    /**
     * @return the replayCause
     */
    public PaymentReplayCauseEnum getReplayCause() {
        return replayCause;
    }

    /**
     * @param replayCause the replayCause to set
     */
    public void setReplayCause(PaymentReplayCauseEnum replayCause) {
        this.replayCause = replayCause;
    }

    /**
     * @return the errorsToReplay
     */
    public String getErrorsToReplay() {
        return errorsToReplay;
    }

    /**
     * @param errorsToReplay the errorsToReplay to set
     */
    public void setErrorsToReplay(String errorsToReplay) {
        this.errorsToReplay = errorsToReplay;
    }

    @Override
    public String toString() {
        return "PaymentGateway [type=" + type + ", paymentMethodType=" + paymentMethodType + ", scriptInstance=" + (scriptInstance == null ? null : scriptInstance.getCode())
                + ", implementationClassName=" + implementationClassName + ", applicationEL=" + applicationEL + ", Country=" + (country == null ? null : country.getCountryCode())
                + ", tradingCurrency=" + (tradingCurrency == null ? null : tradingCurrency.getCurrencyCode()) + ", cardType=" + cardType + "]";
    }
}
