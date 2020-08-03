package org.meveo.api.dto.response.billing;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import org.meveo.api.dto.billing.CustomSubscriptionsListDto;
import org.meveo.api.dto.response.SearchResponse;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "SubscriptionsListResponse")
@XmlAccessorType(FIELD)
public final class CustomSubscriptionsListResponseDto extends SearchResponse {

    private static final long serialVersionUID = 5980154480190489705L;

    public CustomSubscriptionsListDto subscriptions;

    public CustomSubscriptionsListDto getSubscriptions() {
        if (subscriptions == null) {
            subscriptions = new CustomSubscriptionsListDto();
        }
        return subscriptions;
    }

    public void setSubscriptions(CustomSubscriptionsListDto subscriptions) {
        this.subscriptions = subscriptions;
    }

    @Override
    public String toString() {
        return "ListSubscriptionResponseDto [subscriptions=" + subscriptions + ", toString()=" + super.toString() + "]";
    }
}