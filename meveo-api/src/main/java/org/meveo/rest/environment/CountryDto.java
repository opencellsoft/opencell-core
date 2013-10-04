package org.meveo.rest.environment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 * @since Oct 4, 2013
 **/
@XmlRootElement(name = "country")
@XmlAccessorType(XmlAccessType.FIELD)
public class CountryDto extends BaseDto {

	@XmlElement(required = true)
	private String countryCode;

	@XmlElement(required = true)
	private String name;

	@XmlElement(required = true)
	private String currencyCode;

	private String languageCode;

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

}
