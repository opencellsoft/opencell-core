package org.meveo.api.dto.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.billing.ServicesToActivateDto;

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
	private ServicesToActivateDto servicesToActivateDto = new ServicesToActivateDto();

	public String getSubscription() {
		return subscription;
	}

	public void setSubscription(String subscription) {
		this.subscription = subscription;
	}

	public ServicesToActivateDto getServicesToActivateDto() {
		return servicesToActivateDto;
	}

	public void setServicesToActivateDto(ServicesToActivateDto servicesToActivateDto) {
		this.servicesToActivateDto = servicesToActivateDto;
	}

}
