package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CountryDto;

/**
 * The Class GetCountryResponse.
 *
 * @author Edward P. Legaspi
 * @since Oct 7, 2013
 * @deprecated will be renammed to GettTradingCountryResponse
 */
@XmlRootElement(name = "GetCountryResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCountryResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7308813550235264178L;

    /** The country. */
    private CountryDto country;

    /**
     * Instantiates a new gets the country response.
     */
    public GetCountryResponse() {
        super();
    }

    /**
     * Gets the country.
     *
     * @return the country
     */
    public CountryDto getCountry() {
        return country;
    }

    /**
     * Sets the country.
     *
     * @param country the new country
     */
    public void setCountry(CountryDto country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "GetCountryResponse [country=" + country + ", toString()=" + super.toString() + "]";
    }
}