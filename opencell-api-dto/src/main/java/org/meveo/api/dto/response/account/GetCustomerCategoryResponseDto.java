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

package org.meveo.api.dto.response.account;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.CustomerCategoryDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * GetCustomerCategoryResponse Dto.
 *
 * @author akadid abdelmounaim
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "GetCustomerCategoryResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCustomerCategoryResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8824614133076085044L;

    /** The customer category. */
    private CustomerCategoryDto customerCategory;

    /**
     * Instantiates a new gets the customer category response dto.
     */
    public GetCustomerCategoryResponseDto() {
        super();
    }

    /**
     * Gets the customer category.
     *
     * @return the customer category
     */
    public CustomerCategoryDto getCustomerCategory() {
        return customerCategory;
    }

    /**
     * Sets the customer category.
     *
     * @param customerCategory the new customer category
     */
    public void setCustomerCategory(CustomerCategoryDto customerCategory) {
        this.customerCategory = customerCategory;
    }

    @Override
    public String toString() {
        return "GetCustomerCategoryResponse [customerCategory=" + customerCategory + ", toString()=" + super.toString() + "]";
    }
}