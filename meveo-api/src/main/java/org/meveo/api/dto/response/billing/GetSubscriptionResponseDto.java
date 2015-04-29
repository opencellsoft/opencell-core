package org.meveo.api.dto.response.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.SubscriptionDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetSubscriptionResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetSubscriptionResponseDto extends BaseResponse {

	private static final long serialVersionUID = 5811304676103235597L;

	private SubscriptionDto subscription = new SubscriptionDto();

	public SubscriptionDto getSubscription() {
		return subscription;
	}

	public void setSubscription(SubscriptionDto subscription) {
		this.subscription = subscription;
	}

	@Override
	public String toString() {
		return "GetSubscriptionResponseDto [subscription=" + subscription + "]";
	}

}
