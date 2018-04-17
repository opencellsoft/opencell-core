package org.meveo.api.dto.response;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CountryIsoDto;

/**
 * The Class GetCountriesIsoResponse.
 *
 * @author Edward P. Legaspi
 * @since Aug 1, 2017
 */
@XmlRootElement(name = "GetCountriesIsoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCountriesIsoResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8391118981393102116L;

    /** The countries. */
    private List<CountryIsoDto> countries;

    /**
     * Instantiates a new gets the countries iso response.
     */
    public GetCountriesIsoResponse() {
        super();
    }

    /**
     * Gets the countries.
     *
     * @return the countries
     */
    public List<CountryIsoDto> getCountries() {
        return countries;
    }

    /**
     * Sets the countries.
     *
     * @param countries the new countries
     */
    public void setCountries(List<CountryIsoDto> countries) {
        this.countries = countries;
    }

    @Override
    public String toString() {
        return "GetCountriesIsoResponse [countries=" + countries + "]";
    }
}