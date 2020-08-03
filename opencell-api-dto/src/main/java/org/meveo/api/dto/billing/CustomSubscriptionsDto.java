package org.meveo.api.dto.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class CustomSubscriptionsDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4086241876387501134L;

    /** The subscription. */
    private List<CustomSubscriptionDto> subscription;

    /**
     * Gets the subscription.
     *
     * @return the subscription
     */
    public List<CustomSubscriptionDto> getSubscription() {
        if (subscription == null) {
            subscription = new ArrayList<>();
        }

        return subscription;
    }

    /**
     * Sets the subscription.
     *
     * @param subscription the new subscription
     */
    public void setSubscription(List<CustomSubscriptionDto> subscription) {
        this.subscription = subscription;
    }

    @Override
    public String toString() {
        return "SubscriptionsDto [subscription=" + subscription + "]";
    }

}