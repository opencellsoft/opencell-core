package org.meveo.api.dto.billing;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;

/**
 * The Class OperationSubscriptionRequestDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "OperationSubscriptionRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class OperationSubscriptionRequestDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4477259461644796968L;

    /** The subscription code. */
    @XmlElement(required = true)
    private String subscriptionCode;

    /** The action date. */
    private Date actionDate;

    /**
     * Gets the subscription code.
     *
     * @return the subscription code
     */
    public String getSubscriptionCode() {
        return subscriptionCode;
    }

    /**
     * Sets the subscription code.
     *
     * @param subscriptionCode the new subscription code
     */
    public void setSubscriptionCode(String subscriptionCode) {
        this.subscriptionCode = subscriptionCode;
    }

    /**
     * Gets the action date.
     *
     * @return the action date
     */
    public Date getActionDate() {
        return actionDate;
    }

    /**
     * Sets the action date.
     *
     * @param suspensionDate the new action date
     */
    public void setActionDate(Date suspensionDate) {
        this.actionDate = suspensionDate;
    }

    @Override
    public String toString() {
        return "OperationSubscriptionRequestDto  [subscriptionCode=" + subscriptionCode + ", actionDate=" + actionDate + "]";
    }

}