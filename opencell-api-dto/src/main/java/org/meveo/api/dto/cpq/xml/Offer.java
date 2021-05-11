package org.meveo.api.dto.cpq.xml;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.cpq.AttributeDTO;
import org.meveo.model.cpq.offer.QuoteOffer;

@XmlAccessorType(XmlAccessType.FIELD)
public class Offer {
	
	@XmlElement
	private String code;
	@XmlElement
	private String description; 
	 private List<AttributeDTO> attributes;
	private CustomFieldsDto customFields;
	
	
	
	
	public Offer(String code, String description, QuoteOffer quoteOffer,CustomFieldsDto customFields) {
		super();
		this.code = code;
		this.description = description;
		this.attributes =quoteOffer.getOfferTemplate().getAttributes().stream()
                .map(AttributeDTO::new)
                .collect(Collectors.toList());
		this.customFields = customFields;
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

	public List<AttributeDTO> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<AttributeDTO> attributes) {
		this.attributes = attributes;
	}
	public CustomFieldsDto getCustomFields() {
		return customFields;
	}
	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}




}
