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