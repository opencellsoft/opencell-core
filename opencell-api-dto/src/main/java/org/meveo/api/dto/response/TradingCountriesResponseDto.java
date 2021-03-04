package org.meveo.api.dto.response;


import org.meveo.api.dto.TradingCountriesDto;
import org.meveo.api.dto.TradingCountryDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class CountriesResponseDto.
 *
 * @author Thang Nguyen
 */
@XmlRootElement(name = "CountriesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class TradingCountriesResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6134470575443721802L;

    /** The countries DTO. */
    private TradingCountriesDto tradingCountriesDto = new TradingCountriesDto();

    /**
     * Constructor of CountriesResponseDto.
     */
    public TradingCountriesResponseDto() {
    }

    public TradingCountriesResponseDto(GenericSearchResponse<TradingCountryDto> searchResponse) {
        super(searchResponse.getPaging());
        this.tradingCountriesDto.setCountry(searchResponse.getSearchResults());
    }

    /**
     * Get the countries DTO.
     *
     * @return the countries DTO
     */
    public TradingCountriesDto getTradingCountries() {
        return tradingCountriesDto;
    }

    /**
     * Sets the countries DTO.
     *
     * @param tradingCountriesDto the countries DTO
     */
    public void setTradingCountries(TradingCountriesDto tradingCountriesDto) {
        this.tradingCountriesDto = tradingCountriesDto;
    }

    @Override
    public String toString() {
        return "ListTradingCountriesResponseDto [countries=" + tradingCountriesDto + ", toString()=" + super.toString() + "]";
    }
}
