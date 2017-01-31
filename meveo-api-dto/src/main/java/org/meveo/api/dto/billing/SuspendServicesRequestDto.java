package org.meveo.api.dto.billing;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "SuspendServicesRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class SuspendServicesRequestDto extends BaseDto {

	private static final long serialVersionUID = 1150993171011072506L;

	@XmlElement(required = true)
	private String subscriptionCode;

	@XmlElementWrapper(name="ListServiceToSuspend")
    @XmlElement(name="ServiceToSuspend")
	private List<ServiceToSuspendDto> servicesToSuspend = new ArrayList<ServiceToSuspendDto>();

	public String getSubscriptionCode() {
		return subscriptionCode;
	}

	public void setSubscriptionCode(String subscriptionCode) {
		this.subscriptionCode = subscriptionCode;
	}

	public List<ServiceToSuspendDto> getServicesToSuspend() {
		return servicesToSuspend;
	}

	public void setServicesToSuspend(List<ServiceToSuspendDto> servicesToSuspend) {
		this.servicesToSuspend = servicesToSuspend;
	}

	@Override
	public String toString() {
		return "SuspendServicesRequestDto [subscriptionCode=" + subscriptionCode + ", servicesToSuspend=" + servicesToSuspend + "]";
	}

	

}
