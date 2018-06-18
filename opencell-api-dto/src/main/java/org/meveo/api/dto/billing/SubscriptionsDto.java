package org.meveo.api.dto.billing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * The Class SubscriptionsDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriptionsDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4086241876387501134L;

    /** The subscription. */
    private List<SubscriptionDto> subscription;

    /**
     * Gets the subscription.
     *
     * @return the subscription
     */
    public List<SubscriptionDto> getSubscription() {
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
    public void setSubscription(List<SubscriptionDto> subscription) {
        this.subscription = subscription;
    }

    @Override
    public String toString() {
        return "SubscriptionsDto [subscription=" + subscription + "]";
    }

}