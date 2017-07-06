package org.meveo.api.dto.billing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriptionsListDto implements Serializable {

    private static final long serialVersionUID = 4086241876387501134L;

    private int listSize;

    private List<SubscriptionDto> subscription;

    public List<SubscriptionDto> getSubscription() {
        if (subscription == null) {
            subscription = new ArrayList<SubscriptionDto>();
        }

        return subscription;
    }

    public void setSubscription(List<SubscriptionDto> subscription) {
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
