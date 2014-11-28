package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "OfferTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferTemplateDto implements Serializable {

	private static final long serialVersionUID = 9156372453581362595L;

	@XmlAttribute(required = true)
	private String code;

	@XmlAttribute(required = true)
	private String description;

	private boolean disabled;
	private List<String> serviceTemplates;

	public OfferTemplateDto() {

	}

	public OfferTemplateDto(OfferTemplate e) {
		code = e.getCode();
		description = e.getDescription();
		disabled = e.isDisabled();

		if (e.getServiceTemplates() != null
				&& e.getServiceTemplates().size() > 0) {
			serviceTemplates = new ArrayList<String>();

			for (ServiceTemplate st : e.getServiceTemplates()) {
				serviceTemplates.add(st.getCode());
			}
		}
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public List<String> getServiceTemplates() {
		return serviceTemplates;
	}

	public void setServiceTemplates(List<String> serviceTemplates) {
		this.serviceTemplates = serviceTemplates;
	}

	@Override
	public String toString() {
		return "OfferTemplateDto [code=" + code + ", description="
				+ description + ", disabled=" + disabled
				+ ", serviceTemplates=" + serviceTemplates + "]";
	}

}
