/**
 * 
 */
package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentGatewayTypeEnum;
import org.meveo.model.payments.PaymentMethodEnum;

/**
 * The PaymentGateway Dto.
 * 
 * @author anasseh
 *
 */
@XmlRootElement(name = "PaymentGateway")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentGatewayDto extends BusinessDto {

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

    /** The  country code. */
    private String countryCode;

    /** The trading currency code. */
    private String tradingCurrencyCode;

    /** The card type. */
    private CreditCardTypeEnum cardType;

    private CustomFieldsDto customFields;

    /**
     * Instantiates a new payment gateway dto.
     */
    public PaymentGatewayDto() {

    }

    /**
     * Instantiates a new payment gateway dto from the entity.
     * @param paymentGateway payment gateway instance.
     */
    public PaymentGatewayDto(PaymentGateway paymentGateway) {
        this.id = paymentGateway.getId();
        this.applicationEL = paymentGateway.getApplicationEL();
        this.cardType = paymentGateway.getCardType();
        this.code = paymentGateway.getCode();
        this.description = paymentGateway.getDescription();
        this.implementationClassName = paymentGateway.getImplementationClassName();
        this.paymentMethodType = paymentGateway.getPaymentMethodType();
        this.scriptInstanceCode = paymentGateway.getScriptInstance() == null ? null : paymentGateway.getScriptInstance().getCode();
        this.countryCode = paymentGateway.getCountry() == null ? null : paymentGateway.getCountry().getCountryCode();
        this.tradingCurrencyCode = paymentGateway.getTradingCurrency() == null ? null : paymentGateway.getTradingCurrency().getCurrencyCode();
        this.type = paymentGateway.getType();
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
     * @return the countryCode
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * @param countryCode the countryCode to set
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * @return the tradingCurrencyCode
     */
    public String getTradingCurrencyCode() {
        return tradingCurrencyCode;
    }

    /**
     * @param tradingCurrencyCode the tradingCurrencyCode to set
     */
    public void setTradingCurrencyCode(String tradingCurrencyCode) {
        this.tradingCurrencyCode = tradingCurrencyCode;
    }

    /**
     * @return the customFields
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * @param customFields the customFields to set
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    @Override
    public String toString() {
        return "PaymentGatewayDto [id=" + id + ", code=" + code + ", description=" + description + ", updatedCode=" + updatedCode + ", type=" + type + ", paymentMethodType="
                + paymentMethodType + ", scriptInstanceCode=" + scriptInstanceCode + ", implementationClassName=" + implementationClassName + ", applicationEL=" + applicationEL
                + ", countryCode=" + countryCode + ", tradingCurrencyCode=" + tradingCurrencyCode + ", cardType=" + cardType + "]";
    }

}
