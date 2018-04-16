package org.meveo.api.dto.account;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.mediation.Access;

/**
 * The Class AccessDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "Access")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccessDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6495211234062070223L;

    /** Code. */
    @XmlAttribute(required = false)
    private String code;

    /** Subscription. */
    @XmlElement(required = true)
    private String subscription;

    /** Starting date. */
    private Date startDate;

    /** Ending date. */
    private Date endDate;

    /** Custom fields. */
    @XmlElement(required = false)
    private CustomFieldsDto customFields;

    /**
     * Instantiates a new access dto.
     */
    public AccessDto() {

    }

    /**
     * Instantiates a new access dto.
     *
     * @param e the e
     * @param customFieldInstances the custom field instances
     */
    public AccessDto(Access e, CustomFieldsDto customFieldInstances) {
        code = e.getAccessUserId();
        startDate = e.getStartDate();
        endDate = e.getEndDate();

        if (e.getSubscription() != null) {
            subscription = e.getSubscription().getCode();
        }

        customFields = customFieldInstances;
    }

    /**
     * Gets the start date.
     *
     * @return the start date
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date.
     *
     * @param startDate the new start date
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the end date.
     *
     * @return the end date
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date.
     *
     * @param endDate the new end date
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Gets the subscription.
     *
     * @return the subscription
     */
    public String getSubscription() {
        return subscription;
    }

    /**
     * Sets the subscription.
     *
     * @param subscription the new subscription
     */
    public void setSubscription(String subscription) {
        this.subscription = subscription;
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
    
    @Override
    public String toString() {
        return "AccessDto [code=" + code + ", subscription=" + subscription + ", startDate=" + startDate + ", endDate=" + endDate + ", customFields=" + customFields + "]";
    }    
}