package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CountryIsoDto;

/**
 * The Class GetCountryIsoResponse.
 *
 * @author Edward P. Legaspi
 * @since Oct 7, 2013
 */
@XmlRootElement(name = "GetCountryIsoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCountryIsoResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7308813550235264178L;

    /** The country. */
    private CountryIsoDto country;

    /**
     * Instantiates a new gets the country iso response.
     */
    public GetCountryIsoResponse() {
        super();
    }

    /**
     * Gets the country.
     *
     * @return the country
     */
    public CountryIsoDto getCountry() {
        return country;
    }

    /**
     * Sets the country.
     *
     * @param country the new country
     */
    public void setCountry(CountryIsoDto country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "GetCountryIsoResponse [country=" + country + ", toString()=" + super.toString() + "]";
    }
}