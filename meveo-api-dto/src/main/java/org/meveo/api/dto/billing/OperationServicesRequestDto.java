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
@XmlRootElement(name = "OperationServicesRequestDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class OperationServicesRequestDto extends BaseDto {

	private static final long serialVersionUID = 1150993171011072506L;

	@XmlElement(required = true)
	private String subscriptionCode;

	@XmlElementWrapper(name="ListServiceToUpdate")
    @XmlElement(name="serviceToUpdate")
	private List<ServiceToUpdateDto> servicesToUpdate = new ArrayList<ServiceToUpdateDto>();

	public String getSubscriptionCode() {
		return subscriptionCode;
	}

	public void setSubscriptionCode(String subscriptionCode) {
		this.subscriptionCode = subscriptionCode;
	}

	public List<ServiceToUpdateDto> getServicesToUpdate() {
		return servicesToUpdate;
	}

	public void setServicesToUpdate(List<ServiceToUpdateDto> servicesToUpdate) {
		this.servicesToUpdate = servicesToUpdate;
	}

	@Override
	public String toString() {
		return "OperationServicesRequestDto [subscriptionCode=" + subscriptionCode + ", servicesToUpdate=" + servicesToUpdate + "]";
	}

	

}
