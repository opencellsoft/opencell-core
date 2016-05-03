package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.Country;
import org.meveo.model.billing.TradingCountry;

/**
 * @author Edward P. Legaspi
 * @since Oct 4, 2013
 **/
@XmlRootElement(name = "Country")
@XmlAccessorType(XmlAccessType.FIELD)
public class CountryDto extends BaseDto {

	private static final long serialVersionUID = -4175660113940481232L;

	@XmlAttribute(required = true)
	private String countryCode;

	@XmlAttribute()
	private String nameEn;
	
	@XmlAttribute()
	private String nameFr;

	@XmlElement(required = true)
	private String currencyCode;
	
	@XmlElement(required = true)
	private String languageCode;

	public CountryDto() {

	}

	public CountryDto(Country e) {
		countryCode = e.getCountryCode();
		nameEn = e.getDescriptionEn();
		nameFr = e.getDescriptionFr();
		currencyCode = e.getCurrency().getCurrencyCode();

		if (e.getLanguage() != null) {
			languageCode = e.getLanguage().getLanguageCode();
		}
	}

	public CountryDto(TradingCountry e) {
		countryCode = e.getCountryCode();
		nameEn = e.getPrDescription();

		if (e.getCountry() != null && e.getCountry().getCurrency() != null) {
			currencyCode = e.getCountry().getCurrency().getCurrencyCode();
		}

		if (e.getCountry() != null && e.getCountry().getLanguage() != null) {
			languageCode = e.getCountry().getLanguage().getLanguageCode();
		}
	}

	public CountryDto(TradingCountry e, Country c) {
		countryCode = e.getCountryCode();
		nameEn = e.getPrDescription();

		currencyCode = c.getCurrency().getCurrencyCode();

		if (c.getLanguage() != null) {
			languageCode = c.getLanguage().getLanguageCode();
		}
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getNameEn() {
		return nameEn;
	}

	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}

	public String getNameFr() {
		return nameFr;
	}

	public void setNameFr(String nameFr) {
		this.nameFr = nameFr;
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

	public String toString() {
		return "CountryDto [countryCode=" + countryCode + 
				", nameEn=" + nameEn + 
				", nameFr=" + nameFr + 
				", currencyCode=" + currencyCode +
				", languageCode=" + languageCode + "]";
	}

}
