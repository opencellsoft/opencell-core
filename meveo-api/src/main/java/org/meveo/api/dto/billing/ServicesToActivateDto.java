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
	private List<ServiceToActivateDto> service;

	public List<ServiceToActivateDto> getService() {
		if (service == null) {
			service = new ArrayList<>();
		}
		return service;
	}

	public void setService(List<ServiceToActivateDto> services) {
		this.service = services;
	}

	@Override
	public String toString() {
		return "ServicesToActivateDto [service=" + service + "]";
	}

}
