package org.meveo.api.dto.catalog;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 * @created 2 Oct 2017
 */
@XmlRootElement(name = "BSMConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class BSMConfigurationDto {

	@XmlAttribute
	private String code;

	/**
	 * We used this to configure the custom fields for BSM services.
	 */
	@XmlElementWrapper(name = "services")
	@XmlElement(name = "service")
	private List<ServiceConfigurationDto> services;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<ServiceConfigurationDto> getServices() {
		return services;
	}

	public void setServices(List<ServiceConfigurationDto> services) {
		this.services = services;
	}

}
