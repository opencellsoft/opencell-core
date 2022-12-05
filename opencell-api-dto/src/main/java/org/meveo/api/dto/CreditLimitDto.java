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

package org.meveo.api.dto;

import java.math.BigDecimal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;


/**
 * The Class CreditLimitDto.
 *
 * @author Edward P. Legaspi
 * @since Nov 13, 2013
 */
@XmlRootElement(name = "CreditLimit")
@XmlAccessorType(XmlAccessType.FIELD)
public class CreditLimitDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7319728721914566412L;

    /** The organization id. */
    private String organizationId;
    
    /** The credit limit. */
    private BigDecimal creditLimit;

    /**
     * Gets the organization id.
     *
     * @return the organization id
     */
    public String getOrganizationId() {
        return organizationId;
    }

    /**
     * Sets the organization id.
     *
     * @param organizationId the new organization id
     */
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    /**
     * Gets the credit limit.
     *
     * @return the credit limit
     */
    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    /**
     * Sets the credit limit.
     *
     * @param creditLimit the new credit limit
     */
    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

}
