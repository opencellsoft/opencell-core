package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CatMessages")
@XmlAccessorType(XmlAccessType.FIELD)
public class CatMessagesDto extends BaseDto {

	private static final long serialVersionUID = 1L;
	
	@XmlAttribute(required = true)
	private String objectType;
	
	@XmlAttribute(required = true)
	private String languageCode;
	
	@XmlAttribute(required = true)
	private String entityCode;
	
	@XmlAttribute(required = true)
	private String descriptionTranslation;
	
	private String basicDescription;
	
	private String catMessagesCode;
	
	public String getObjectType() {
		return objectType;
	}
	
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public String getDescriptionTranslation() {
		return descriptionTranslation;
	}

	public void setDescriptionTranslation(String descriptionTranslation) {
		this.descriptionTranslation = descriptionTranslation;
	}

	public String getBasicDescription() {
		return basicDescription;
	}

	public void setBasicDescription(String basicDescription) {
		this.basicDescription = basicDescription;
	}

	public String getCatMessagesCode() {
		return catMessagesCode;
	}

	public void setCatMessagesCode(String catMessagesCode) {
		this.catMessagesCode = catMessagesCode;
	}

}
