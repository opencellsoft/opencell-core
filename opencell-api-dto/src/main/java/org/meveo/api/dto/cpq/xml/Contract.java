package org.meveo.api.dto.cpq.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.api.dto.CustomFieldDto;

@XmlAccessorType(XmlAccessType.FIELD)
public class Contract {

	private String code;
	private String description;
	private CustomFieldDto customField;
	
	public Contract(org.meveo.model.cpq.contract.Contract contract, CustomFieldDto customFieldDto) {
		this.code = contract.getCode();
		this.description = contract.getDescription();
		this.customField = customFieldDto;
	}
	
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the customField
	 */
	public CustomFieldDto getCustomField() {
		return customField;
	}
	/**
	 * @param customField the customField to set
	 */
	public void setCustomField(CustomFieldDto customField) {
		this.customField = customField;
	}
	
	
}
