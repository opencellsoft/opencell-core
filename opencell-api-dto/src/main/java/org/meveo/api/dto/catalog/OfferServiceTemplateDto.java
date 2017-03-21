package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.ServiceTemplate;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "OfferServiceTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferServiceTemplateDto implements Serializable{

	private static final long serialVersionUID = 7137259235916807339L;
	private ServiceTemplateDto serviceTemplate;
	private Boolean mandatory;

	@XmlElementWrapper(name = "incompatibleServices")
	@XmlElement(name = "incompatibleServiceTemplate")
	private List<ServiceTemplateDto> incompatibleServices = new ArrayList<>();

	public OfferServiceTemplateDto() {

	}

	public OfferServiceTemplateDto(OfferServiceTemplate e) {
		if (e.getServiceTemplate() != null) {
			serviceTemplate = new ServiceTemplateDto(e.getServiceTemplate(), null);
		}
		mandatory = e.isMandatory();
		if (e.getIncompatibleServices() != null) {
			for (ServiceTemplate st : e.getIncompatibleServices()) {
				incompatibleServices.add(new ServiceTemplateDto(st.getCode()));
			}
		}
	}

	public void setMandatory(Boolean mandatory) {
		this.mandatory = mandatory;
	}

	public List<ServiceTemplateDto> getIncompatibleServices() {
		return incompatibleServices;
	}

	public void setIncompatibleServices(List<ServiceTemplateDto> incompatibleServices) {
		this.incompatibleServices = incompatibleServices;
	}

	@Override
	public String toString() {
		return "OfferServiceTemplateDto [serviceTemplate=" + serviceTemplate + ", mandatory=" + mandatory + ", incompatibleServices=" + incompatibleServices + "]";
	}

	public ServiceTemplateDto getServiceTemplate() {
		return serviceTemplate;
	}

	public void setServiceTemplate(ServiceTemplateDto serviceTemplate) {
		this.serviceTemplate = serviceTemplate;
	}

	public Boolean getMandatory() {
		return mandatory;
	}

}
