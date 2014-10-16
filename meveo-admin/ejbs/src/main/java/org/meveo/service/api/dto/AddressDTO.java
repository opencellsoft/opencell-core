/*
* (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.meveo.service.api.dto;

import java.io.Serializable;

/**
 * Contains address information
 * 
 * @author Andrius Karpavicius
 */
public class AddressDTO implements Serializable {

    private static final long serialVersionUID = 8775799547993130837L;

    protected String address1;

    protected String address2;

    protected String address3;

    protected String zipCode;

    protected String city;

    protected String country;

    protected String state;

    public AddressDTO(String address1, String address2, String address3, String zipCode, String city, String country, String state) {
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.zipCode = zipCode;
        this.city = city;
        this.country = country;
        this.state = state;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getAddress3() {
        return address3;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }
}