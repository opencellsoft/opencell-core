package org.meveo.api.dto.catalog;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.meveo.api.dto.CustomFieldDto;
import org.meveo.model.crm.custom.CustomFieldValue;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ServiceConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceConfigurationDto {

	@NotNull
	@XmlAttribute
	private String code;

	@XmlAttribute
	private String description;
	
	@XmlElementWrapper(name = "parameters")
	@XmlElement(name = "parameter")
	private List<CustomFieldDto> customFields;
		
	/**
	 * Used in the GUI side only.
	 */
	@XmlTransient
	@JsonIgnore
	private Map<String, List<CustomFieldValue>> cfValues;
	
	private boolean mandatory;
	
	/**
	 * Tells us that this service is linked to a BusinessServiceModel.
	 */
	private boolean instantiatedFromBSM;
	
	/**
	 * Use when matching service template in bsm vs offer.
	 */
	private boolean match = false;

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

	public List<CustomFieldDto> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(List<CustomFieldDto> customFields) {
		this.customFields = customFields;
	}

	@Override
	public String toString() {
		return "ServiceConfigurationDto [code=" + code + ", description=" + description + ", customFields=" + customFields + "]";
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public boolean isInstantiatedFromBSM() {
		return instantiatedFromBSM;
	}

	public void setInstantiatedFromBSM(boolean instantiatedFromBSM) {
		this.instantiatedFromBSM = instantiatedFromBSM;
	}

	public boolean isMatch() {
		return match;
	}

	public void setMatch(boolean match) {
		this.match = match;
	}

	public Map<String, List<CustomFieldValue>> getCfValues() {
		return cfValues;
	}

	public void setCfValues(Map<String, List<CustomFieldValue>> cfValues) {
		this.cfValues = cfValues;
	}

}
