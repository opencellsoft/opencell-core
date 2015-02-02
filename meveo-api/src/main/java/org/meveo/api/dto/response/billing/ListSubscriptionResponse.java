package org.meveo.api.dto.response.billing;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.SubscriptionDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ListSubscriptionResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public final class ListSubscriptionResponse extends BaseResponse {

	private static final long serialVersionUID = 5980154480190489704L;
	
	public List<SubscriptionDto> subscriptions;

	public List<SubscriptionDto> getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(List<SubscriptionDto> subscriptions) {
		this.subscriptions = subscriptions;
	}

}
