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
@XmlRootElement(name = "ServicesToInstantiate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServicesToInstantiateDto implements Serializable {

	private static final long serialVersionUID = -6088111478916521480L;

	@XmlElement(required = true)
	private List<ServiceToInstantiateDto> service;

	public List<ServiceToInstantiateDto> getService() {
		if (service == null) {
			service = new ArrayList<>();
		}
		return service;
	}

	public void setService(List<ServiceToInstantiateDto> services) {
		this.service = services;
	}

	@Override
	public String toString() {
		return "ServicesToInstantiateDto [service=" + service + "]";
	}

}
