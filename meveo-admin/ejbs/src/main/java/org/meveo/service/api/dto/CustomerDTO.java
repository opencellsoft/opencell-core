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
 * Contains information about a customer
 * 
 * @author Andrius Karpavicius
 * 
 */
public class CustomerDTO implements Serializable {

    private static final long serialVersionUID = 3799753737053316393L;

    private Long id;

    private String code;

    private String name;

    private String externalRef1;

    private String externalRef2;

    private AddressDTO address;

    private String brandCode;

    private String categoryCode;

    public CustomerDTO(Long id, String code, String name, String externalRef1, String externalRef2, AddressDTO address, String brandCode, String categoryCode) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.externalRef1 = externalRef1;
        this.externalRef2 = externalRef2;
        this.address = address;
        this.brandCode = brandCode;
        this.categoryCode = categoryCode;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getExternalRef1() {
        return externalRef1;
    }

    public String getExternalRef2() {
        return externalRef2;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public String getBrandCode() {
        return brandCode;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

}
