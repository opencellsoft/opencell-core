package org.meveo.api.dto.billing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ServicesToActivate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServicesToActivateDto implements Serializable {

	private static final long serialVersionUID = -6088111478916521480L;
	
	@XmlElement(required = true)
	private List<ServiceToActivateDto> services;
	
	public List<ServiceToActivateDto> getServices() {
		if (services == null) {
			services = new ArrayList<>();
		}
		return services;
	}

	public void setServices(List<ServiceToActivateDto> services) {
		this.services = services;
	}
	
}
