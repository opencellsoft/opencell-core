package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CurrencyDto;

/**
 * The Class GetCurrencyResponse.
 *
 * @author Edward P. Legaspi
 * @since Oct 7, 2013
 * @deprecated will be rennamed to GetTradingCurrencyResponse
 */
@XmlRootElement(name = "GetCurrencyResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCurrencyResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5595545533673878857L;

    /** The currency. */
    private CurrencyDto currency;

    /**
     * Instantiates a new gets the currency response.
     */
    public GetCurrencyResponse() {
        super();
    }

    /**
     * Gets the currency.
     *
     * @return the currency
     */
    public CurrencyDto getCurrency() {
        return currency;
    }

    /**
     * Sets the currency.
     *
     * @param currency the new currency
     */
    public void setCurrency(CurrencyDto currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "GetCurrencyResponse [currency=" + currency + ", toString()=" + super.toString() + "]";
    }
}
