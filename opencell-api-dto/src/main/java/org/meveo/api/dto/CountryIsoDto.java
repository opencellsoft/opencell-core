package org.meveo.api.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.Country;


/**
 * The Class CountryIsoDto.
 *
 * @author Edward P. Legaspi
 * @since Oct 4, 2013
 */
@XmlRootElement(name = "CountryIso")
@XmlAccessorType(XmlAccessType.FIELD)
public class CountryIsoDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4175660113940481232L;

    /** The country code. */
    @XmlAttribute(required = true)
    private String countryCode;

    /** The description. */
    @XmlAttribute()
    private String description;

    /** The language descriptions. */
    private List<LanguageDescriptionDto> languageDescriptions;

    /** The currency code. */
    @XmlElement(required = true)
    private String currencyCode;

    /** The language code. */
    @XmlElement(required = true)
    private String languageCode;

    /**
     * Instantiates a new country iso dto.
     */
    public CountryIsoDto() {

    }

    /**
     * Instantiates a new country iso dto.
     *
     * @param e the country entity
     */
    public CountryIsoDto(Country e) {
        countryCode = e.getCountryCode();
        description = e.getDescription();
        setLanguageDescriptions(LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(e.getDescriptionI18n()));
        currencyCode = e.getCurrency().getCurrencyCode();

        if (e.getLanguage() != null) {
            languageCode = e.getLanguage().getLanguageCode();
        }
    }

    /**
     * Gets the country code.
     *
     * @return the country code
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Sets the country code.
     *
     * @param countryCode the new country code
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
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
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the language descriptions.
     *
     * @return the languageDescriptions
     */
    public List<LanguageDescriptionDto> getLanguageDescriptions() {
        return languageDescriptions;
    }

    /**
     * Sets the language descriptions.
     *
     * @param languageDescriptions the languageDescriptions to set
     */
    public void setLanguageDescriptions(List<LanguageDescriptionDto> languageDescriptions) {
        this.languageDescriptions = languageDescriptions;
    }

    /**
     * Gets the currency code.
     *
     * @return the currency code
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * Sets the currency code.
     *
     * @param currencyCode the new currency code
     */
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    /**
     * Gets the language code.
     *
     * @return the language code
     */
    public String getLanguageCode() {
        return languageCode;
    }

    /**
     * Sets the language code.
     *
     * @param languageCode the new language code
     */
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

}
