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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ModuleItem;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.scripts.ScriptInstance;

/**
 * The PaymentGateway on opencell exists in 2 types {@link org.meveo.model.payments.PaymentGatewayTypeEnum PaymentGatewayTypeEnum}: <lu>
 * <li>Custom: The administrator can define the implementation in a script that extends {@link org.meveo.service.script.payment.PaymentScript PaymentScript}.</li>
 * <li>Natif: The business implementation code is available on the opencell core, currently the available PSP are Inginico Global Collect, and SEPA format payment file generation
 * and Paynum format.</li> </lu>
 *
 *
 * @author anasseh
 * @since Opencell 4.8
 */

@Entity
@ModuleItem
@CustomFieldEntity(cftCodePrefix = "PAYMENT_GW")
@Table(name = "ar_payment_gateway")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_payment_gateway_seq"), })
public class PaymentGateway extends BusinessCFEntity {

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
    @JoinColumn(name = "trading_country_id")
    private TradingCountry tradingCountry;

    /** The trading currency. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_currency_id")
    private TradingCurrency tradingCurrency;

    /** The card type. */
    @Enumerated(EnumType.STRING)
    @Column(name = "card_type")
    private CreditCardTypeEnum cardType;

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
     * @return the tradingCountry
     */
    public TradingCountry getTradingCountry() {
        return tradingCountry;
    }

    /**
     * @param tradingCountry the tradingCountry to set
     */
    public void setTradingCountry(TradingCountry tradingCountry) {
        this.tradingCountry = tradingCountry;
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

    @Override
    public String toString() {
        return "PaymentGateway [type=" + type + ", paymentMethodType=" + paymentMethodType + ", scriptInstance=" + (scriptInstance == null ? null : scriptInstance.getCode())
                + ", implementationClassName=" + implementationClassName + ", applicationEL=" + applicationEL + ", tradingCountry=" + tradingCountry + ", tradingCurrency="
                + tradingCurrency + ", cardType=" + cardType + "]";
    }
}
