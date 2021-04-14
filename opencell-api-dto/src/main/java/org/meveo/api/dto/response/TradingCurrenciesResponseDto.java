package org.meveo.api.dto.response;

import org.meveo.api.dto.CurrenciesDto;
import org.meveo.api.dto.CurrencyDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * The Class TradingCurrenciesResponseDto.
 *
 * @author Thang Nguyen
 */
@XmlRootElement(name = "TradingCurrenciesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class TradingCurrenciesResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6134470575443721802L;

    /** The currencies DTO. */
    private CurrenciesDto currenciesDto = new CurrenciesDto();

    /**
     * Constructor of TradingCurrenciesResponseDto.
     */
    public TradingCurrenciesResponseDto() {
    }

    public TradingCurrenciesResponseDto(GenericSearchResponse<CurrencyDto> searchResponse) {
        super(searchResponse.getPaging());
        this.currenciesDto.setCurrency(searchResponse.getSearchResults());
    }

    /**
     * Get the currencies DTO.
     *
     * @return the currencies DTO
     */
    public CurrenciesDto getTradingCurrencies() {
        return currenciesDto;
    }

    /**
     * Sets the currencies DTO.
     *
     * @param currenciesDto the currencies DTO
     */
    public void setTradingCurrencies(CurrenciesDto currenciesDto) {
        this.currenciesDto = currenciesDto;
    }

    @Override
    public String toString() {
        return "ListCurrenciesResponseDto [currencies=" + currenciesDto + ", toString()=" + super.toString() + "]";
    }
}
