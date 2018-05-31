package org.meveo.api.dto.response.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.SubscriptionsDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class SubscriptionsResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "SubscriptionsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public final class SubscriptionsResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5980154480190489704L;

    /** The subscriptions. */
    public SubscriptionsDto subscriptions;

    /**
     * Gets the subscriptions.
     *
     * @return the subscriptions
     */
    public SubscriptionsDto getSubscriptions() {
        return subscriptions;
    }

    /**
     * Sets the subscriptions.
     *
     * @param subscriptions the new subscriptions
     */
    public void setSubscriptions(SubscriptionsDto subscriptions) {
        this.subscriptions = subscriptions;
    }

    @Override
    public String toString() {
        return "ListSubscriptionResponseDto [subscriptions=" + subscriptions + ", toString()=" + super.toString() + "]";
    }
}