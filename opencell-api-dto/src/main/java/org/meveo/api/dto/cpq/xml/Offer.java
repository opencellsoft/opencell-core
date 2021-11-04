package org.meveo.api.dto.cpq.xml;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.cpq.AttributeDTO;
import org.meveo.api.dto.cpq.QuoteProductDTO;
import org.meveo.model.cpq.offer.QuoteOffer;

@XmlAccessorType(XmlAccessType.FIELD)
public class Offer {
	
	@XmlElement
	private String code;
	@XmlElement
	private String description; 
	@XmlElement
	private String name;
	
	 private List<Attribute> attributes;
	 private List<Product> products;
	private CustomFieldsDto customFields;
	
	
	
	
	public Offer(QuoteOffer quoteOffer,CustomFieldsDto customFields) {
		super();
		this.code = quoteOffer.getOfferTemplate().getCode();
		this.description = quoteOffer.getOfferTemplate().getDescription();
		this.name=quoteOffer.getOfferTemplate().getName();
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


	
	public List<Attribute> getAttributes() {
		return attributes;
	}


	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}


	public CustomFieldsDto getCustomFields() {
		return customFields;
	}
	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}


	public List<Product> getProducts() {
		return products;
	}


	public void setProducts(List<Product> products) {
		this.products = products;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
	
	

	



}
