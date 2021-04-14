package org.meveo.api.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class TradingCountriesDto.
 *
 * @author Thang Nguyen
 */
public class TradingCountriesDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8690397660261914992L;

    /** The country. */
    private List<TradingCountryDto> tradingCountries;

    /**
     * Gets the country.
     *
     * @return the country
     */
    public List<TradingCountryDto> getCountry() {
        if (tradingCountries == null)
            tradingCountries = new ArrayList<TradingCountryDto>();
        return tradingCountries;
    }

    /**
     * Sets the tradingCountries.
     *
     * @param tradingCountries the new country
     */
    public void setCountry(List<TradingCountryDto> tradingCountries) {
        this.tradingCountries = tradingCountries;
    }

}
