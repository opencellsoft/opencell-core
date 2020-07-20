package org.meveo.api.dto.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class CustomSubscriptionsListDto implements Serializable {

    private static final long serialVersionUID = 4086241876387501135L;

    private int listSize;

    private List<CustomSubscriptionDto> subscription;

    public List<CustomSubscriptionDto> getSubscription() {
        if (subscription == null) {
            subscription = new ArrayList<>();
        }

        return subscription;
    }

    public void setSubscription(List<CustomSubscriptionDto> subscription) {
        this.subscription = subscription;
    }

    public int getListSize() {
        return listSize;
    }

    public void setListSize(int listSize) {
        this.listSize = listSize;
    }

    @Override
    public String toString() {
        return "SubscriptionsDto [subscription=" + subscription + "]";
    }
}