package org.meveo.api.dto.response.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.SubscriptionsListDto;
import org.meveo.api.dto.response.SearchResponse;

/**
 * The Class SubscriptionsListResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "SubscriptionsListResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public final class SubscriptionsListResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5980154480190489704L;

    /** The subscriptions. */
    public SubscriptionsListDto subscriptions;

    /**
     * Gets the subscriptions.
     *
     * @return the subscriptions
     */
    public SubscriptionsListDto getSubscriptions() {
        if (subscriptions == null) {
            subscriptions = new SubscriptionsListDto();
        }
        return subscriptions;
    }

    /**
     * Sets the subscriptions.
     *
     * @param subscriptions the new subscriptions
     */
    public void setSubscriptions(SubscriptionsListDto subscriptions) {
        this.subscriptions = subscriptions;
    }

    @Override
    public String toString() {
        return "ListSubscriptionResponseDto [subscriptions=" + subscriptions + ", toString()=" + super.toString() + "]";
    }
}