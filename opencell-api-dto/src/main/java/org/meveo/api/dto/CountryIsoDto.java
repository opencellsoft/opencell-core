package org.meveo.api.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.Country;

/**
 * @author Edward P. Legaspi
 * @since Oct 4, 2013
 **/
@XmlRootElement(name = "CountryIso")
@XmlAccessorType(XmlAccessType.FIELD)
public class CountryIsoDto extends BaseDto {

	private static final long serialVersionUID = -4175660113940481232L;

	@XmlAttribute(required = true)
	private String countryCode;

	@XmlAttribute()
	private String description;
	
	private List<LanguageDescriptionDto> languageDescriptions;

	@XmlElement(required = true)
	private String currencyCode;
	
	@XmlElement(required = true)
	private String languageCode;

	public CountryIsoDto() {

	}

	public CountryIsoDto(Country e) {
		countryCode = e.getCountryCode();
		description = e.getDescription();
		setLanguageDescriptions(LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(e.getDescriptionI18n()));
		currencyCode = e.getCurrency().getCurrencyCode();

		if (e.getLanguage() != null) {
			languageCode = e.getLanguage().getLanguageCode();
		}
	}


	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	/**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the languageDescriptions
     */
    public List<LanguageDescriptionDto> getLanguageDescriptions() {
        return languageDescriptions;
    }

    /**
     * @param languageDescriptions the languageDescriptions to set
     */
    public void setLanguageDescriptions(List<LanguageDescriptionDto> languageDescriptions) {
        this.languageDescriptions = languageDescriptions;
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
