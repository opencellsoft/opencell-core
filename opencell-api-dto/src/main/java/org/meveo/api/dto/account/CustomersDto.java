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

package org.meveo.api.dto.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

/**
 * The Class CustomersDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
// @FilterResults(propertyToFilter = "customer", itemPropertiesToFilter = { @FilterProperty(property = "code", entityClass = Customer.class) })
public class CustomersDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1693325835765290126L;

    /** The customer. */
    private List<CustomerDto> customer;
    
    /** The total number of records. */
    private Long totalNumberOfRecords;

    /**
     * Gets the customer.
     *
     * @return the customer
     */
    public List<CustomerDto> getCustomer() {
        if (customer == null) {
            customer = new ArrayList<CustomerDto>();
        }

        return customer;
    }

    /**
     * Sets the customer.
     *
     * @param customer the new customer
     */
    public void setCustomer(List<CustomerDto> customer) {
        this.customer = customer;
    }

    /**
     * Gets the total number of records.
     *
     * @return the total number of records
     */
    public Long getTotalNumberOfRecords() {
        return totalNumberOfRecords;
    }

    /**
     * Sets the total number of records.
     *
     * @param totalNumberOfRecords the new total number of records
     */
    public void setTotalNumberOfRecords(Long totalNumberOfRecords) {
        this.totalNumberOfRecords = totalNumberOfRecords;
    }
    
    @Override
    public String toString() {
        return "CustomersDto [customer=" + customer + "]";
    }
}