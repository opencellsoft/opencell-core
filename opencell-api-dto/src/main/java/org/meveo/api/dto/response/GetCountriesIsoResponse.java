/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.dto.response;

import org.meveo.api.dto.CountryIsoDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class GetCountriesIsoResponse.
 *
 * @author Edward P. Legaspi
 * @since Aug 1, 2017
 */
@XmlRootElement(name = "GetCountriesIsoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCountriesIsoResponse extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8391118981393102116L;

    /** The countries. */
    private List<CountryIsoDto> countries = new ArrayList<>();

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