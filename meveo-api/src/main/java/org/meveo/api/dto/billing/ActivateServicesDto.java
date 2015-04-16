package org.meveo.api.dto.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ActivateServices")
@XmlAccessorType(XmlAccessType.FIELD)
public class ActivateServicesDto extends BaseDto {

	private static final long serialVersionUID = 1150993171011072506L;

	@XmlElement(required = true)
	private String subscription;

	@XmlElement
	private ServicesToActivateDto servicesToActivate = new ServicesToActivateDto();

	public String getSubscription() {
		return subscription;
	}

	public void setSubscription(String subscription) {
		this.subscription = subscription;
	}

	public ServicesToActivateDto getServicesToActivateDto() {
		return servicesToActivate;
	}

	public void setServicesToActivateDto(ServicesToActivateDto servicesToActivate) {
		this.servicesToActivate = servicesToActivate;
	}

	@Override
	public String toString() {
		return "ActivateServicesDto [subscription=" + subscription + ", servicesToActivate=" + servicesToActivate + "]";
	}

}
