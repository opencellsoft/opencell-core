package org.meveo.api.dto.billing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Edward P. Legaspi
 **/
@XmlType(name = "Subscriptions")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriptionsDto implements Serializable {

	private static final long serialVersionUID = 4086241876387501134L;

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

	@Override
	public String toString() {
		return "SubscriptionsDto [subscription=" + subscription + "]";
	}

}
