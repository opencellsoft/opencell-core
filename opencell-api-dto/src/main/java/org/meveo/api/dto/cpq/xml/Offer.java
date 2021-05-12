package org.meveo.api.dto.cpq.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.meveo.api.dto.CustomFieldDto;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.GroupedAttributes;

@XmlAccessorType(XmlAccessType.FIELD)
public class Offer {
	
	@XmlElement
	private String code;
	@XmlElement
	private String description;
	private Attribute attribute;
	private CustomFieldDto customField;
	
	
	
	
	public Offer(String code, String description, Attribute attribute, CustomFieldDto customField) {
		super();
		this.code = code;
		this.description = description;
		this.attribute = attribute;
		this.customField = customField;
	}
	/**
	 * @return the productLine
	 */




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




	public Attribute getAttribute() {
		return attribute;
	}




	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}




	public CustomFieldDto getCustomField() {
		return customField;
	}




	public void setCustomField(CustomFieldDto customField) {
		this.customField = customField;
	}

}
