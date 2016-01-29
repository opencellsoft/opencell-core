package org.meveo.api.dto.catalog;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

@XmlRootElement(name = "BusinessOfferModel")
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessOfferModelDto extends BaseDto {

	private static final long serialVersionUID = -7023791262640948222L;

	@NotNull
	@XmlAttribute(required = true)
	private String code;

	@XmlAttribute
	private String description;

	@NotNull
	@XmlElement(required = true)
	private String offerTemplateCode;

	private String scriptCode;

	public String getOfferTemplateCode() {
		return offerTemplateCode;
	}

	public void setOfferTemplateCode(String offerTemplateCode) {
		this.offerTemplateCode = offerTemplateCode;
	}

	@Override
	public String toString() {
		return "BusinessOfferModelDto [code=" + code + ", description=" + description + ", offerTemplateCode="
				+ offerTemplateCode + ", scriptCode=" + scriptCode + "]";
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getScriptCode() {
		return scriptCode;
	}

	public void setScriptCode(String scriptCode) {
		this.scriptCode = scriptCode;
	}


}
