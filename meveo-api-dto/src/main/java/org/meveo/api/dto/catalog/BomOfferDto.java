package org.meveo.api.dto.catalog;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CustomFieldsDto;

@XmlRootElement(name = "BomOffer")
@XmlAccessorType(XmlAccessType.FIELD)
public class BomOfferDto extends BaseDto {

	private static final long serialVersionUID = 4557706201829891403L;

	@NotNull
	@XmlAttribute(required = true)
	private String bomCode;

	@XmlAttribute
	private String description;

	@XmlElementWrapper(name = "parameters")
	@XmlElement(name = "parameter")
	private CustomFieldsDto offerCustomFields;

	private String prefix;

	@XmlElementWrapper(name = "servicesToActivate")
	@XmlElement(name = "serviceToActivate")
	private List<ServiceConfigurationDto> servicesToActivate;

	public String getBomCode() {
		return bomCode;
	}

	public void setBomCode(String bomCode) {
		this.bomCode = bomCode;
	}

	public CustomFieldsDto getOfferCustomFields() {
		return offerCustomFields;
	}

	public void setOfferCustomFields(CustomFieldsDto offerCustomFields) {
		this.offerCustomFields = offerCustomFields;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public String toString() {
		return "BomOfferDto [bomCode=" + bomCode + ", description=" + description + ", offerCustomFields=" + offerCustomFields + ", prefix=" + prefix + ", servicesToActivate="
				+ servicesToActivate + "]";
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ServiceConfigurationDto> getServicesToActivate() {
		return servicesToActivate;
	}

	public void setServicesToActivate(List<ServiceConfigurationDto> servicesToActivate) {
		this.servicesToActivate = servicesToActivate;
	}

}
