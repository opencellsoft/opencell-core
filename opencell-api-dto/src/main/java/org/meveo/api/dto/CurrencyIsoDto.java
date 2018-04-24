package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.admin.Currency;

// TODO: Auto-generated Javadoc
/**
 * The Class CurrencyIsoDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "CurrencyIso")
@XmlAccessorType(XmlAccessType.FIELD)
public class CurrencyIsoDto extends BaseDto {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 9143645109603442839L;

	/** The code. */
	@XmlAttribute(required = true)
	private String code;

	/** The description. */
	private String description;

	/**
	 * Instantiates a new currency iso dto.
	 */
	public CurrencyIsoDto() {

	}


	/**
	 * Instantiates a new currency iso dto.
	 *
	 * @param currency the currency
	 */
	public CurrencyIsoDto(Currency currency) {
		code = currency.getCurrencyCode();
		description = currency.getDescriptionEn();
	}

	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Sets the code.
	 *
	 * @param code the new code
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CurrencyIsoDto [code=" + code + ", description=" + description + "]";
	}

}
