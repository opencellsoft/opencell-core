/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.api.dto;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.SubCategoryInvoiceAgregate;

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
    private String discountPlanItemCode;

    /** The discount percent. */
    private BigDecimal discountPercent;

    /**
     * Instantiates a new sub category invoice aggregate dto.
     *
     * @param subCategoryInvoiceAgregate the SubCategoryInvoiceAgregate entity
     */
    public DiscountInvoiceAggregateDto(SubCategoryInvoiceAgregate subCategoryInvoiceAgregate) {
        super(subCategoryInvoiceAgregate, false);
        discountPlanItemCode = subCategoryInvoiceAgregate.getDiscountPlanItem().getCode();
        discountPercent = subCategoryInvoiceAgregate.getDiscountPercent();
    }

    /**
     * Instantiates a new discount invoice aggregate dto.
     */
    public DiscountInvoiceAggregateDto() {

    }

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