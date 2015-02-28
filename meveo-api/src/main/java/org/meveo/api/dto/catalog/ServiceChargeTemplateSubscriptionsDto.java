package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Edward P. Legaspi
 **/
@XmlType(name = "ServiceChargeTemplateSubscriptions")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceChargeTemplateSubscriptionsDto implements Serializable {

	private static final long serialVersionUID = -8759911503295449204L;

	private List<ServiceChargeTemplateSubscriptionDto> serviceChargeTemplateSubscription;

	public List<ServiceChargeTemplateSubscriptionDto> getServiceChargeTemplateSubscription() {
		if (serviceChargeTemplateSubscription == null)
			serviceChargeTemplateSubscription = new ArrayList<ServiceChargeTemplateSubscriptionDto>();
		return serviceChargeTemplateSubscription;
	}

	public void setServiceChargeTemplateSubscription(
			List<ServiceChargeTemplateSubscriptionDto> serviceChargeTemplateSubscription) {
		this.serviceChargeTemplateSubscription = serviceChargeTemplateSubscription;
	}

	@Override
	public String toString() {
		return "ServiceChargeTemplateSubscriptionsDto [serviceChargeTemplateSubscription="
				+ serviceChargeTemplateSubscription + "]";
	}

}
