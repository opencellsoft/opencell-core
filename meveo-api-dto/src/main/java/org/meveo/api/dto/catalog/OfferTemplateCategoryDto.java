package org.meveo.api.dto.catalog;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "OfferCategory")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferTemplateCategoryDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String code;
	
	private String description;
	
	private String name;
	
	private String imageByteValue;
	
	private String offerTemplateCategoryCode;
	
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOfferTemplateCategoryCode() {
		return offerTemplateCategoryCode;
	}

	public void setOfferTemplateCategoryCode(String offerTemplateCategoryCode) {
		this.offerTemplateCategoryCode = offerTemplateCategoryCode;
	}

	public String getImageByteValue() {
		return imageByteValue;
	}

	public void setImageByteValue(String imageByteValue) {
		this.imageByteValue = imageByteValue;
	}

	@Override
	public String toString() {
		return "OfferTemplateCategoryDto [code=" + code + ", description="
				+ description + ", name=" + name + ", imageByteValue="
				+ imageByteValue + ", offerTemplateCategoryCode="
				+ offerTemplateCategoryCode + "]";
	}
}