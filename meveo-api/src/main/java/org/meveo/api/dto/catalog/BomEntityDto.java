package org.meveo.api.dto.catalog;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "BomEntity")
@XmlAccessorType(XmlAccessType.FIELD)
public class BomEntityDto extends BaseDto {

	private static final long serialVersionUID = -7023791262640948222L;

	@NotNull
	@XmlAttribute(required = true)
	private String bomCode;

	@XmlAttribute
	private String description;

	@NotNull
	@XmlElement(required = true)
	private String offerTemplateCode;

	private String scriptInstanceCode;

	public String getOfferTemplateCode() {
		return offerTemplateCode;
	}

	public void setOfferTemplateCode(String offerTemplateCode) {
		this.offerTemplateCode = offerTemplateCode;
	}

	public String getScriptInstanceCode() {
		return scriptInstanceCode;
	}

	public void setScriptInstanceCode(String scriptInstanceCode) {
		this.scriptInstanceCode = scriptInstanceCode;
	}

	public String getBomCode() {
		return bomCode;
	}

	public void setBomCode(String bomCode) {
		this.bomCode = bomCode;
	}

	@Override
	public String toString() {
		return "BomEntityDto [bomCode=" + bomCode + ", description=" + description + ", offerTemplateCode="
				+ offerTemplateCode + ", scriptInstanceCode=" + scriptInstanceCode + "]";
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
