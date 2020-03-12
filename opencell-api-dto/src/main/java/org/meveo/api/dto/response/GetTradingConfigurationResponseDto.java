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

package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CountriesDto;
import org.meveo.api.dto.CurrenciesDto;
import org.meveo.api.dto.LanguagesDto;

/**
 * The Class GetTradingConfigurationResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetTradingConfigurationResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetTradingConfigurationResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -598052725975586031L;

    /** The countries. */
    private CountriesDto countries = new CountriesDto();
    
    /** The currencies. */
    private CurrenciesDto currencies = new CurrenciesDto();
    
    /** The languages. */
    private LanguagesDto languages = new LanguagesDto();;

    /**
     * Gets the countries.
     *
     * @return the countries
     */
    public CountriesDto getCountries() {
        return countries;
    }

    /**
     * Sets the countries.
     *
     * @param countries the new countries
     */
    public void setCountries(CountriesDto countries) {
        this.countries = countries;
    }

    /**
     * Gets the currencies.
     *
     * @return the currencies
     */
    public CurrenciesDto getCurrencies() {
        return currencies;
    }

    /**
     * Sets the currencies.
     *
     * @param currencies the new currencies
     */
    public void setCurrencies(CurrenciesDto currencies) {
        this.currencies = currencies;
    }

    /**
     * Gets the languages.
     *
     * @return the languages
     */
    public LanguagesDto getLanguages() {
        return languages;
    }

    /**
     * Sets the languages.
     *
     * @param languages the new languages
     */
    public void setLanguages(LanguagesDto languages) {
        this.languages = languages;
    }

    @Override
    public String toString() {
        return "GetTradingConfigurationResponseDto [countries=" + countries + ", currencies=" + currencies + ", languages=" + languages + ", toString()=" + super.toString() + "]";
    }
}