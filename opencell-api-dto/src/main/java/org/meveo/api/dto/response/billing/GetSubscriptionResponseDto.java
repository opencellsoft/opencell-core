package org.meveo.api.dto.response.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.SubscriptionDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetSubscriptionResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetSubscriptionResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetSubscriptionResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5811304676103235597L;

    /** The subscription. */
    private SubscriptionDto subscription = new SubscriptionDto();

    /**
     * Gets the subscription.
     *
     * @return the subscription
     */
    public SubscriptionDto getSubscription() {
        return subscription;
    }

    /**
     * Sets the subscription.
     *
     * @param subscription the new subscription
     */
    public void setSubscription(SubscriptionDto subscription) {
        this.subscription = subscription;
    }

    @Override
    public String toString() {
        return "GetSubscriptionResponseDto [subscription=" + subscription + "]";
    }
}