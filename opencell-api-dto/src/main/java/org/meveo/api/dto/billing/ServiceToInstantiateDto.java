package org.meveo.api.dto.billing;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.catalog.ServiceTemplate;

/**
 * The Class ServiceToInstantiateDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "ServiceToInstantiate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceToInstantiateDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3815026205495621916L;

    /** The code. */
    @XmlAttribute(required = true)
    private String code;

    /** The Service Instance code. */
    @XmlAttribute
    private String overrideCode;

    /** The description. */
    @XmlAttribute
    private String description;

    /** The quantity. */
    @XmlElement(required = true)
    private BigDecimal quantity = BigDecimal.ONE;

    /** The subscription date. */
    private Date subscriptionDate;

    /** The charge instance overrides. */
    private ChargeInstanceOverridesDto chargeInstanceOverrides;

    /** The custom fields. */
    private CustomFieldsDto customFields;

    /** The service template. */
    @XmlTransient
    // @ApiModelProperty(hidden = true)
    private ServiceTemplate serviceTemplate;

    /** The rate until date. */
    private Date rateUntilDate;
    
    
    /** The amount PS. */
    private BigDecimal amountPS;
    
    /** The calendar PS code. */
    private String calendarPSCode;
    

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
     * Gets the service instance code.
     *
     * @return the overrideCode
     */
    public String getOverrideCode() {
        return overrideCode;
    }

    /**
     * Sets the service instance code.
     *
     * @param overrideCode the new service instance code.
     */
    public void setOverrideCode(String overrideCode) {
        this.overrideCode = overrideCode;
    }

    /**
     * Gets the quantity.
     *
     * @return the quantity
     */
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity.
     *
     * @param quantity the new quantity
     */
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the subscription date.
     *
     * @return the subscription date
     */
    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    /**
     * Sets the subscription date.
     *
     * @param subscriptionDate the new subscription date
     */
    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    /**
     * Gets the service template.
     *
     * @return the service template
     */
    public ServiceTemplate getServiceTemplate() {
        return serviceTemplate;
    }

    /**
     * Sets the service template.
     *
     * @param serviceTemplate the new service template
     */
    public void setServiceTemplate(ServiceTemplate serviceTemplate) {
        this.serviceTemplate = serviceTemplate;
    }

    /**
     * Gets the charge instance overrides.
     *
     * @return the charge instance overrides
     */
    public ChargeInstanceOverridesDto getChargeInstanceOverrides() {
        return chargeInstanceOverrides;
    }

    /**
     * Sets the charge instance overrides.
     *
     * @param chargeInstanceOverrides the new charge instance overrides
     */
    public void setChargeInstanceOverrides(ChargeInstanceOverridesDto chargeInstanceOverrides) {
        this.chargeInstanceOverrides = chargeInstanceOverrides;
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
     * Gets the rate until date.
     *
     * @return the rate until date
     */
    public Date getRateUntilDate() {
        return rateUntilDate;
    }

    /**
     * Sets the rate until date.
     *
     * @param rateUtilDate the new rate until date
     */
    public void setRateUntilDate(Date rateUtilDate) {
        this.rateUntilDate = rateUtilDate;
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
     * Gets the amount PS.
     *
     * @return the amountPS
     */
    public BigDecimal getAmountPS() {
        return amountPS;
    }

    /**
     * Sets the amount PS.
     *
     * @param amountPS the amountPS to set
     */
    public void setAmountPS(BigDecimal amountPS) {
        this.amountPS = amountPS;
    }

    /**
     * Gets the calendar PS code.
     *
     * @return the calendarPSCode
     */
    public String getCalendarPSCode() {
        return calendarPSCode;
    }

    /**
     * Sets the calendar PS code.
     *
     * @param calendarPSCode the calendarPSCode to set
     */
    public void setCalendarPSCode(String calendarPSCode) {
        this.calendarPSCode = calendarPSCode;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("ServiceToInstantiateDto [code=%s, quantity=%s, subscriptionDate=%s, chargeInstanceOverrides=%s, customFields=%s]", code, quantity, subscriptionDate,
            chargeInstanceOverrides, customFields);
    }
}