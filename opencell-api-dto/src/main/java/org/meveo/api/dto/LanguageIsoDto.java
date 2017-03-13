package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.Language;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "LanguageIso")
@XmlAccessorType(XmlAccessType.FIELD)
public class LanguageIsoDto extends BaseDto {

	private static final long serialVersionUID = 725968016559888810L;

	@XmlAttribute(required = true)
	private String code;
	private String description;

	public LanguageIsoDto() {

	}


	public LanguageIsoDto(Language e) {
		code = e.getLanguageCode();
		description = e.getDescriptionEn();
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

	@Override
	public String toString() {
		return "LanguageIsoDto [code=" + code + ", description=" + description + "]";
	}

}
