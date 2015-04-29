package org.meveo.api.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 * @since Nov 13, 2013
 **/
@XmlRootElement(name = "SubscriptionWithCreditLimitUpdate")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriptionWithCreditLimitUpdateDto extends
		SubscriptionWithCreditLimitDto {

	private static final long serialVersionUID = -6700315102709912658L;

	private List<ServiceToTerminateDto> servicesToTerminate;

	public List<ServiceToTerminateDto> getServicesToTerminate() {
		return servicesToTerminate;
	}

	public void setServicesToTerminate(
			List<ServiceToTerminateDto> servicesToTerminate) {
		this.servicesToTerminate = servicesToTerminate;
	}

}
