package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CurrencyIsoDto;

/**
 * The Class GetTradingCurrencyResponse.
 *
 * @author Edward P. Legaspi
 * @since Oct 7, 2013
 */
@XmlRootElement(name = "GetTradingCurrencyResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetTradingCurrencyResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5595545533673878857L;

    /** The currency. */
    private CurrencyIsoDto currency;

    /**
     * Instantiates a new gets the trading currency response.
     */
    public GetTradingCurrencyResponse() {
        super();
    }

    /**
     * Gets the currency.
     *
     * @return the currency
     */
    public CurrencyIsoDto getCurrency() {
        return currency;
    }

    /**
     * Sets the currency.
     *
     * @param currency the new currency
     */
    public void setCurrency(CurrencyIsoDto currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "GetTradingCurrencyResponse [currency=" + currency + ", toString()=" + super.toString() + "]";
    }
}