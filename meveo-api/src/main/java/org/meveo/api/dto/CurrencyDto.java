package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.TradingCurrency;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "Currency")
@XmlAccessorType(XmlAccessType.FIELD)
public class CurrencyDto extends BaseDto {

	private static final long serialVersionUID = 9143645109603442839L;

	@XmlAttribute(required = true)
	private String code;

	private String description;

	public CurrencyDto() {

	}

	public CurrencyDto(TradingCurrency e) {
		code = e.getCurrencyCode();
		description = e.getPrDescription();
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
		return "CurrencyDto [code=" + code + ", description=" + description + "]";
	}

}
