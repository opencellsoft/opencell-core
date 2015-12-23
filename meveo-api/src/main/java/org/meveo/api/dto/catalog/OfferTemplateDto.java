package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
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

	@XmlAttribute(required = true)
	private String description;

	private boolean disabled;
	private ServiceTemplatesDto serviceTemplates = new ServiceTemplatesDto();
	
	private CustomFieldsDto customFields = new CustomFieldsDto();
	
	private String subscriptionScriptCode;
	private String terminationScriptCode;

	public OfferTemplateDto() {

	}

	public OfferTemplateDto(OfferTemplate e, Map<String, List<CustomFieldInstance>> customFieldInstances) {
		code = e.getCode();
		description = e.getDescription();
		disabled = e.isDisabled();
		if (e.getSubscriptionScript() != null) {
			subscriptionScriptCode = e.getSubscriptionScript().getCode();
		}
		if (e.getTerminationScript() != null) {
			terminationScriptCode = e.getTerminationScript().getCode();
		}

		if (e.getServiceTemplates() != null && e.getServiceTemplates().size() > 0) {
			for (ServiceTemplate st : e.getServiceTemplates()) {
				serviceTemplates.getServiceTemplate().add(new ServiceTemplateDto(st.getCode()));
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
				+ ", serviceTemplates=" + serviceTemplates + ", customFields=" + customFields + "]";
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

	public String getSubscriptionScriptCode() {
		return subscriptionScriptCode;
	}

	public void setSubscriptionScriptCode(String subscriptionScriptCode) {
		this.subscriptionScriptCode = subscriptionScriptCode;
	}

	public String getTerminationScriptCode() {
		return terminationScriptCode;
	}

	public void setTerminationScriptCode(String terminationScriptCode) {
		this.terminationScriptCode = terminationScriptCode;
	}

}
