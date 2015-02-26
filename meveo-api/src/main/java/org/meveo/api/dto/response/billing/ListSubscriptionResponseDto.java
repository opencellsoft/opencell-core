package org.meveo.api.dto.response.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.SubscriptionsDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ListSubscriptionResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public final class ListSubscriptionResponseDto extends BaseResponse {

	private static final long serialVersionUID = 5980154480190489704L;
	
	public SubscriptionsDto subscriptions;

	public SubscriptionsDto getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(SubscriptionsDto subscriptions) {
		this.subscriptions = subscriptions;
	}

	@Override
	public String toString() {
		return "ListSubscriptionResponseDto [subscriptions=" + subscriptions + ", toString()=" + super.toString() + "]";
	}

}
