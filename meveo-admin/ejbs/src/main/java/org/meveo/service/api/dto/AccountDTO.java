/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
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