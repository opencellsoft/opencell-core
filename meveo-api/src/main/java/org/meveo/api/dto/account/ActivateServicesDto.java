package org.meveo.api.dto.account;

import java.math.BigDecimal;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "Customer")
@XmlAccessorType(XmlAccessType.FIELD)
public class ActivateServicesDto extends BaseDto {

	private static final long serialVersionUID = 1150993171011072506L;

	@XmlElement(required = true)
	private String subscription;

	@XmlElement(required = true)
	private HashMap<String, BigDecimal> services;

	public String getSubscription() {
		return subscription;
	}

	public void setSubscription(String subscription) {
		this.subscription = subscription;
	}

	public HashMap<String, BigDecimal> getServices() {
		return services;
	}

	public void setServices(HashMap<String, BigDecimal> services) {
		this.services = services;
	}

	@Override
	public String toString() {
		return "InstantiateServicesDto [subscription=" + subscription + ", services=" + services + "]";
	}

}
