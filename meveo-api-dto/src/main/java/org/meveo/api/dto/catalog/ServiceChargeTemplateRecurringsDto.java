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
@XmlType(name = "ServiceChargeTemplateRecurrings")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceChargeTemplateRecurringsDto implements Serializable {

	private static final long serialVersionUID = -4649058196780119541L;

	private List<ServiceChargeTemplateRecurringDto> serviceChargeTemplateRecurring;

	public List<ServiceChargeTemplateRecurringDto> getServiceChargeTemplateRecurring() {
		if (serviceChargeTemplateRecurring == null)
			serviceChargeTemplateRecurring = new ArrayList<ServiceChargeTemplateRecurringDto>();
		return serviceChargeTemplateRecurring;
	}

	public void setServiceChargeTemplateRecurring(List<ServiceChargeTemplateRecurringDto> serviceChargeTemplateRecurring) {
		this.serviceChargeTemplateRecurring = serviceChargeTemplateRecurring;
	}

	@Override
	public String toString() {
		return "ServiceChargeTemplateRecurringsDto [serviceChargeTemplateRecurring=" + serviceChargeTemplateRecurring
				+ "]";
	}

}
