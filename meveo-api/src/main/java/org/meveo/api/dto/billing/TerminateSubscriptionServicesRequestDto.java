package org.meveo.api.dto.billing;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "TerminateSubscriptionServicesRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class TerminateSubscriptionServicesRequestDto extends BaseDto {

	private static final long serialVersionUID = 7356243821434866938L;

	@XmlElement(required = true)
	private List<String> services;

	@XmlElement(required = true)
	private String subscriptionCode;

	@XmlElement(required = true)
	private String terminationReason;

	@XmlElement(required = true)
	private Date terminationDate;

	public List<String> getServices() {
		return services;
	}

	public void setServices(List<String> services) {
		this.services = services;
	}

	public String getTerminationReason() {
		return terminationReason;
	}

	public void setTerminationReason(String terminationReason) {
		this.terminationReason = terminationReason;
	}

	public Date getTerminationDate() {
		return terminationDate;
	}

	public void setTerminationDate(Date terminationDate) {
		this.terminationDate = terminationDate;
	}

	public String getSubscriptionCode() {
		return subscriptionCode;
	}

	public void setSubscriptionCode(String subscriptionCode) {
		this.subscriptionCode = subscriptionCode;
	}

	@Override
	public String toString() {
		return "TerminateSubscriptionServicesDto [services=" + services + ", subscriptionCode=" + subscriptionCode
				+ ", terminationReason=" + terminationReason + ", terminationDate=" + terminationDate + "]";
	}

}
