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
