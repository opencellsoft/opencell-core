package org.meveo.api.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "LanguageDescription")
@XmlAccessorType(XmlAccessType.FIELD)
public class LanguageDescriptionDto implements Serializable {

	private static final long serialVersionUID = -4686792860854718893L;

	private String languageCode;
	private String description;

	public LanguageDescriptionDto() {

	}

	public LanguageDescriptionDto(String languageCode, String description) {
		this.languageCode = languageCode;
		this.description = description;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "LanguageDescriptionDto [languageCode=" + languageCode + ", description=" + description + "]";
	}

}
