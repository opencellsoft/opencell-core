/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.Country;
import org.meveo.model.billing.TradingCountry;

/**
 * The Class CountryDto.
 *
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 8.0.0
 * @since Oct 4, 2013
 */
@XmlRootElement(name = "Country")
@XmlAccessorType(XmlAccessType.FIELD)
public class CountryDto extends AuditableEntityDto implements IEnableDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4175660113940481232L;

    /**
     * The country code
     */
    @XmlAttribute(required = true)
    private String countryCode;

    /**
     * The country name
     */
    @XmlAttribute()
    private String name;

    /**
     * The currency code
     */
    @XmlElement(required = true)
    private String currencyCode;

    /**
     * Corresponding language code
     */
    private String languageCode;

    /**
     * Is entity disabled. Value is ignored in Update action - use enable/disable API instead.
     */
    private Boolean disabled;

    /**
     * Instantiates a new country dto.
     */
    public CountryDto() {

    }

    /**
     * Instantiates a new country dto.
     *
     * @param country the Country enntity
     */
    public CountryDto(Country country) {
        super(country);
        countryCode = country.getCountryCode();
        name = country.getDescription();
        
        if(country.getCurrency() != null) {
            currencyCode = country.getCurrency().getCurrencyCode();
        }

        if (country.getLanguage() != null) {
            languageCode = country.getLanguage().getLanguageCode();
        }
    }

    /**
     * Instantiates a new country dto.
     *
     * @param tradingCountry the TradingCountry entity
     */
    public CountryDto(TradingCountry tradingCountry) {
        countryCode = tradingCountry.getCountryCode();
        name = tradingCountry.getDescription();

        if (tradingCountry.getCountry() != null && tradingCountry.getCountry().getCurrency() != null) {
            currencyCode = tradingCountry.getCountry().getCurrency().getCurrencyCode();
        }

        if (tradingCountry.getCountry() != null && tradingCountry.getCountry().getLanguage() != null) {
            languageCode = tradingCountry.getCountry().getLanguage().getLanguageCode();
        }
        disabled = tradingCountry.isDisabled();
    }

    /**
     * Instantiates a new country dto.
     *
     * @param tradingCountry the TradingCountry entity
     * @param country the Country entity
     */
    public CountryDto(TradingCountry tradingCountry, Country country) {
        countryCode = tradingCountry.getCountryCode();
        name = tradingCountry.getDescription();
        
        if(country != null) {
            if(country.getCurrency() != null) {
                currencyCode = country.getCurrency().getCurrencyCode();
            }
            
            if (country.getLanguage() != null) {
                languageCode = country.getLanguage().getLanguageCode();
            }
        }
        disabled = tradingCountry.isDisabled();
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
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "CountryDto [countryCode=" + countryCode + ", name=" + name + ", currencyCode=" + currencyCode + ", languageCode=" + languageCode + ", disabled=" + disabled + "]";
    }

    @Override
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public Boolean isDisabled() {
        return disabled;
    }
}