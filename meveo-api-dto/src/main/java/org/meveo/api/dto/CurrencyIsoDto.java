package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.admin.Currency;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "CurrencyIso")
@XmlAccessorType(XmlAccessType.FIELD)
public class CurrencyIsoDto extends BaseDto {

	private static final long serialVersionUID = 9143645109603442839L;

	@XmlAttribute(required = true)
	private String code;

	private String description;

	public CurrencyIsoDto() {

	}


	public CurrencyIsoDto(Currency e) {
		code = e.getCurrencyCode();
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
		return "CurrencyIsoDto [code=" + code + ", description=" + description + "]";
	}

}
