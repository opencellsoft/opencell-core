package org.meveo.api.dto.billing;

import org.meveo.api.dto.BaseEntityDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class ActivateSubscriptionRequestDto.
 *
 * @author Thang Nguyen
 */
@XmlRootElement(name = "ActivateSubscriptionRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class ActivateSubscriptionRequestDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4477259461644796968L;

    /** The subscription code. */
    @XmlElement(required = true)
    private String subscriptionCode;

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
}
