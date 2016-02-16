package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.crm.CustomFieldInstance;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "OfferTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferTemplateDto implements Serializable {

	private static final long serialVersionUID = 9156372453581362595L;

	@XmlAttribute(required = true)
	private String code;

	@XmlAttribute()
	private String description;

	private boolean disabled;
	private ServiceTemplatesDto serviceTemplates = new ServiceTemplatesDto();
	
	private CustomFieldsDto customFields = new CustomFieldsDto();
	
	private String bomCode;

	public OfferTemplateDto() {

	}

	public OfferTemplateDto(OfferTemplate e, Map<String, List<CustomFieldInstance>> customFieldInstances) {
		code = e.getCode();
		description = e.getDescription();
		disabled = e.isDisabled();
		if (e.getBusinessOfferModel() != null) {
			bomCode = e.getBusinessOfferModel().getCode();
		}
		

		if (e.getOfferServiceTemplates() != null && e.getOfferServiceTemplates().size() > 0) {
			for (OfferServiceTemplate st : e.getOfferServiceTemplates()) {
				serviceTemplates.getServiceTemplate().add(new ServiceTemplateDto(st.getServiceTemplate().getCode()));
			}
		}

		customFields = CustomFieldsDto.toDTO(customFieldInstances);
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

	@Override
	public String toString() {
		return "OfferTemplateDto [code=" + code + ", description=" + description + ", disabled=" + disabled
				+ ", serviceTemplates=" + serviceTemplates + ", customFields=" + customFields + ", bomCode=" + bomCode
				+ "]";
	}

	public ServiceTemplatesDto getServiceTemplates() {
		return serviceTemplates;
	}

	public void setServiceTemplates(ServiceTemplatesDto serviceTemplates) {
		this.serviceTemplates = serviceTemplates;
	}

	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}

	public String getBomCode() {
		return bomCode;
	}

	public void setBomCode(String bomCode) {
		this.bomCode = bomCode;
	}

}
