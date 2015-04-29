package org.meveo.api.dto.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "InstantiateServices")
@XmlAccessorType(XmlAccessType.FIELD)
public class InstantiateServicesDto extends BaseDto {

	private static final long serialVersionUID = 1150993171011072506L;

	@XmlElement(required = true)
	private String subscription;

	@XmlElement
	private ServicesToInstantiateDto servicesToInstantiate = new ServicesToInstantiateDto();

	public String getSubscription() {
		return subscription;
	}

	public void setSubscription(String subscription) {
		this.subscription = subscription;
	}

	@Override
	public String toString() {
		return "InstantiateServicesDto [subscription=" + subscription + ", servicesToInstantiate=" + servicesToInstantiate + "]";
	}

	public ServicesToInstantiateDto getServicesToInstantiate() {
		return servicesToInstantiate;
	}

	public void setServicesToInstantiate(ServicesToInstantiateDto servicesToInstantiate) {
		this.servicesToInstantiate = servicesToInstantiate;
	}

}
