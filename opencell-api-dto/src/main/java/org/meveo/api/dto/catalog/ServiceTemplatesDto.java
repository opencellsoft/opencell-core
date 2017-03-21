package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ServiceTemplates")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceTemplatesDto implements Serializable {

	private static final long serialVersionUID = 4923256325444663405L;

	private List<ServiceTemplateDto> serviceTemplate;

	public List<ServiceTemplateDto> getServiceTemplate() {
		if (serviceTemplate == null)
			serviceTemplate = new ArrayList<ServiceTemplateDto>();
		return serviceTemplate;
	}

	public void setServiceTemplate(List<ServiceTemplateDto> serviceTemplate) {
		this.serviceTemplate = serviceTemplate;
	}

	@Override
	public String toString() {
		return "ServiceTemplatesDto [serviceTemplate=" + serviceTemplate + "]";
	}

}
