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
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name="Access")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccessDto extends BaseDto {

    private static final long serialVersionUID = 6495211234062070223L;

    /**
     * Code
     */
    @XmlAttribute(required = false)
    private String code;

    /**
     * Subscription
     */
    @XmlElement(required = true)
    private String subscription;

    /**
     * Starting date
     */
    private Date startDate;

    /**
     * Ending date
     */
    private Date endDate;

    /**
     * Custom fields
     */
    @XmlElement(required = false)
    private CustomFieldsDto customFields;

    public AccessDto() {

    }

    public AccessDto(Access e, CustomFieldsDto customFieldInstances) {
        code = e.getAccessUserId();
        startDate = e.getStartDate();
        endDate = e.getEndDate();

        if (e.getSubscription() != null) {
            subscription = e.getSubscription().getCode();
        }

        customFields = customFieldInstances;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    @Override
    public String toString() {
        return "AccessDto [code=" + code + ", subscription=" + subscription + ", startDate=" + startDate + ", endDate=" + endDate + ", customFields=" + customFields + "]";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }
}