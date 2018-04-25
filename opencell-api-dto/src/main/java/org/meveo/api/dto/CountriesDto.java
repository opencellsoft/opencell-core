package org.meveo.api.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class CountriesDto.
 *
 * @author Edward P. Legaspi
 * @since Oct 4, 2013
 */
@XmlRootElement(name = "Countries")
@XmlAccessorType(XmlAccessType.FIELD)
public class CountriesDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8690397660261914992L;

    /** The country. */
    private List<CountryDto> country;

    /**
     * Gets the country.
     *
     * @return the country
     */
    public List<CountryDto> getCountry() {
        if (country == null)
            country = new ArrayList<CountryDto>();
        return country;
    }

    /**
     * Sets the country.
     *
     * @param country the new country
     */
    public void setCountry(List<CountryDto> country) {
        this.country = country;
    }

}
