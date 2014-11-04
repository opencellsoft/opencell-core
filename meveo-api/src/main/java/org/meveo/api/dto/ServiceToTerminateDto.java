package org.meveo.api.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 * @since Nov 13, 2013
 **/
@XmlRootElement(name = "ServiceToTerminate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceToTerminateDto extends BaseDto {

	private static final long serialVersionUID = 3267838736094614395L;

	private String serviceId;
	private Date terminationDate;

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public Date getTerminationDate() {
		return terminationDate;
	}

	public void setTerminationDate(Date terminationDate) {
		this.terminationDate = terminationDate;
	}

}
