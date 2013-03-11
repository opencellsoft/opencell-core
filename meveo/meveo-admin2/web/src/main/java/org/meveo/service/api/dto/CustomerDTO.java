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
