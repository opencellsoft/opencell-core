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
 * Contains general account information
 * 
 * @author Andrius Karpavicius
 */
public class AccountDTO implements Serializable {

    private static final long serialVersionUID = -6451615583975154478L;

    private Long id;

    private String code;

    private String externalRef1;

    private String externalRef2;

    private AddressDTO address;

    private String titleCode;

    private String firstName;

    private String lastName;

    public AccountDTO(Long id, String code, String externalRef1, String externalRef2, AddressDTO address, String titleCode, String firstName, String lastName) {
        this.id = id;
        this.code = code;
        this.externalRef1 = externalRef1;
        this.externalRef2 = externalRef2;
        this.address = address;
        this.titleCode = titleCode;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
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

    public String getTitleCode() {
        return titleCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}