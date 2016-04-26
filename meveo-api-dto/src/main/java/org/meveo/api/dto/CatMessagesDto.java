package org.meveo.api.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CatMessages")
@XmlAccessorType(XmlAccessType.FIELD)
public class CatMessagesDto extends BaseDto {

	private static final long serialVersionUID = 1L;
	
	@XmlElement(required = true)
	private String entityClass;
	
	@XmlElement(required = true)
	private String code;
	
	@XmlElement(required = true)
	private String defaultDescription;
	
	@XmlElement
	private List<LanguageDescriptionDto> translatedDescriptions;

	public CatMessagesDto() {
		translatedDescriptions = new ArrayList<>();
	}

	public String getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(String entityClass) {
		this.entityClass = entityClass;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDefaultDescription() {
		return defaultDescription;
	}

	public void setDefaultDescription(String defaultDescription) {
		this.defaultDescription = defaultDescription;
	}

	public List<LanguageDescriptionDto> getTranslatedDescriptions() {
		return translatedDescriptions;
	}

	public void setTranslatedDescriptions(List<LanguageDescriptionDto> translatedDescriptions) {
		this.translatedDescriptions = translatedDescriptions;
	}

}
