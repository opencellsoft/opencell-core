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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.SubCategoryInvoiceAgregate;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Discount invoice aggregate DTO
 *
 * @author Andrius Karpavicius
 * @lastModifiedVersion 5.2
 */
@XmlRootElement(name = "DiscountInvoiceAggregate")
@XmlAccessorType(XmlAccessType.FIELD)
public class DiscountInvoiceAggregateDto extends SubCategoryInvoiceAgregateDto {

    private static final long serialVersionUID = -4415088850335611099L;

    /** The discount plan item code. */
    @Schema(description = "The discount plan item code")
    private String discountPlanItemCode;

    /** The discount percent. */
    @Schema(description = "The discount percent")
    private BigDecimal discountPercent;

    /**
     * Gets the discount plan item code
     *
     * @return Discount plan item code
     */
    public String getDiscountPlanItemCode() {
        return discountPlanItemCode;
    }

    /**
     * Sets the discount plan item code
     *
     * @param discountPlanItemCode Discount plan item code
     */
    public void setDiscountPlanItemCode(String discountPlanItemCode) {
        this.discountPlanItemCode = discountPlanItemCode;
    }

    /**
     * Gets the discount percent
     *
     * @return Discount percent
     */
    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    /**
     * Sets the discount percent
     *
     * @param discountPercent Discount percent
     */
    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }
}