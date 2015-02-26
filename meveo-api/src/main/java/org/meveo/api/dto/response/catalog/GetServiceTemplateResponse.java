package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetServiceTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetServiceTemplateResponse extends BaseResponse {

	private static final long serialVersionUID = -2517820224350375764L;

	private ServiceTemplateDto serviceTemplate;

	public ServiceTemplateDto getServiceTemplate() {
		return serviceTemplate;
	}

	public void setServiceTemplate(ServiceTemplateDto serviceTemplate) {
		this.serviceTemplate = serviceTemplate;
	}

	@Override
	public String toString() {
		return "GetServiceTemplateResponse [serviceTemplate=" + serviceTemplate + ", toString()=" + super.toString()
				+ "]";
	}

}
