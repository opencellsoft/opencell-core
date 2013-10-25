package org.meveo.api.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 * @since Oct 11, 2013
 **/
@XmlRootElement(name = "service")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceDto extends BaseDto {

	private String serviceId;
	private List<DescriptionDto> descriptions;

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public List<DescriptionDto> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(List<DescriptionDto> descriptions) {
		this.descriptions = descriptions;
	}

}
