package org.meveo.api.dto.response;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CurrencyIsoDto;

/**
 * The Class GetCurrenciesIsoResponse.
 *
 * @author Edward P. Legaspi
 * @since Aug 1, 2017
 */
@XmlRootElement(name = "GetCurrenciesIsoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCurrenciesIsoResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 12269486818856166L;

    /** The currencies. */
    private List<CurrencyIsoDto> currencies;

    /**
     * Instantiates a new gets the currencies iso response.
     */
    public GetCurrenciesIsoResponse() {
        super();
    }

    /**
     * Gets the currencies.
     *
     * @return the currencies
     */
    public List<CurrencyIsoDto> getCurrencies() {
        return currencies;
    }

    /**
     * Sets the currencies.
     *
     * @param currencies the new currencies
     */
    public void setCurrencies(List<CurrencyIsoDto> currencies) {
        this.currencies = currencies;
    }

    @Override
    public String toString() {
        return "GetCurrenciesIsoResponse [currencies=" + currencies + "]";
    }
}