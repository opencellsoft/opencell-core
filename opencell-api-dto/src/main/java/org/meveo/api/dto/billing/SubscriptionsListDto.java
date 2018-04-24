package org.meveo.api.dto.billing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * The Class SubscriptionsListDto.
 * 
 * @author anasseh
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriptionsListDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4086241876387501134L;

    /** The list size. */
    private int listSize;

    /** The subscription. */
    private List<SubscriptionDto> subscription;

    /**
     * Gets the subscription.
     *
     * @return the subscription
     */
    public List<SubscriptionDto> getSubscription() {
        if (subscription == null) {
            subscription = new ArrayList<SubscriptionDto>();
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

    /**
     * Gets the list size.
     *
     * @return the list size
     */
    public int getListSize() {
        return listSize;
    }

    /**
     * Sets the list size.
     *
     * @param listSize the new list size
     */
    public void setListSize(int listSize) {
        this.listSize = listSize;
    }

    @Override
    public String toString() {
        return "SubscriptionsDto [subscription=" + subscription + "]";
    }

}