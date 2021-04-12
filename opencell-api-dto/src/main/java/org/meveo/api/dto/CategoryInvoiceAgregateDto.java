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
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Invoice category invoice aggregate DTO
 */
@XmlRootElement(name = "CategoryInvoiceAgregate")
@XmlAccessorType(XmlAccessType.FIELD)
public class CategoryInvoiceAgregateDto extends BaseEntityDto {

    private static final long serialVersionUID = 6165612614574594919L;

    /**
     * The category invoice code
     */
    @XmlElement(required = true)
    @Schema(description = "The category invoice code")
    private String categoryInvoiceCode;

    /** The description */
    @Schema(description = "The description")
    private String description;

    /** The user account code */
    @Schema(description = "The user account code")
    private String userAccountCode;

    /** The item number */
    @Schema(description = "The item number")
    private Integer itemNumber;

    /** The amount without tax */
    @Schema(description = "The amount without tax")
    private BigDecimal amountWithoutTax;

    /** The amount tax */
    @Schema(description = "The amount tax")
    private BigDecimal amountTax;

    /** The amount with tax */
    @Schema(description = "The amount with tax")
    private BigDecimal amountWithTax;

    /**
     * Sub category invoice aggregates
     */
    @XmlElementWrapper
    @XmlElement(name = "subCategoryInvoiceAgregateDto", required = true)
    @Schema(description = "List of Sub category invoice aggregates")
    private List<SubCategoryInvoiceAgregateDto> listSubCategoryInvoiceAgregateDto = new ArrayList<SubCategoryInvoiceAgregateDto>();

    /**
     * Discount aggregates
     */
    @XmlElementWrapper
    @XmlElement(name = "discountAggregate")
    @Schema(description = "List of Discount aggregates")
    private List<DiscountInvoiceAggregateDto> discountAggregates;

    /**
     * Gets the category invoice code.
     *
     * @return the categoryInvoiceCode
     */
    public String getCategoryInvoiceCode() {
        return categoryInvoiceCode;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the category invoice code.
     *
     * @param categoryInvoiceCode the categoryInvoiceCode to set
     */
    public void setCategoryInvoiceCode(String categoryInvoiceCode) {
        this.categoryInvoiceCode = categoryInvoiceCode;
    }

    /**
     * Gets the sub category invoice aggregates
     *
     * @return Sub category invoice aggregates
     */
    public List<SubCategoryInvoiceAgregateDto> getListSubCategoryInvoiceAgregateDto() {
        return listSubCategoryInvoiceAgregateDto;
    }

    /**
     * Sets the sub category invoice aggregates
     *
     * @param listSubCategoryInvoiceAgregateDto Sub category invoice aggregates
     */
    public void setListSubCategoryInvoiceAgregateDto(List<SubCategoryInvoiceAgregateDto> listSubCategoryInvoiceAgregateDto) {
        this.listSubCategoryInvoiceAgregateDto = listSubCategoryInvoiceAgregateDto;
    }

    /**
     * Gets discount aggregates
     * 
     * @return Discount aggregates
     */
    public List<DiscountInvoiceAggregateDto> getDiscountAggregates() {
        return discountAggregates;
    }

    /**
     * Sets discount aggregates
     * 
     * @param discountAggregates discount aggregates
     */
    public void setDiscountAggregates(List<DiscountInvoiceAggregateDto> discountAggregates) {
        this.discountAggregates = discountAggregates;
    }

    /**
     * Gets the user account code.
     *
     * @return the user account code
     */
    public String getUserAccountCode() {
        return userAccountCode;
    }

    /**
     * Sets the user account code.
     *
     * @param userAccountCode the new user account code
     */
    public void setUserAccountCode(String userAccountCode) {
        this.userAccountCode = userAccountCode;
    }

    /**
     * Gets the item number.
     *
     * @return the item number
     */
    public Integer getItemNumber() {
        return itemNumber;
    }

    /**
     * Sets the item number.
     *
     * @param itemNumber the new item number
     */
    public void setItemNumber(Integer itemNumber) {
        this.itemNumber = itemNumber;
    }

    /**
     * Gets the amount without tax.
     *
     * @return the amount without tax
     */
    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    /**
     * Sets the amount without tax.
     *
     * @param amountWithoutTax the new amount without tax
     */
    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    /**
     * Gets the amount tax.
     *
     * @return the amount tax
     */
    public BigDecimal getAmountTax() {
        return amountTax;
    }

    /**
     * Sets the amount tax.
     *
     * @param amountTax the new amount tax
     */
    public void setAmountTax(BigDecimal amountTax) {
        this.amountTax = amountTax;
    }

    /**
     * Gets the amount with tax.
     *
     * @return the amount with tax
     */
    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    /**
     * Sets the amount with tax.
     *
     * @param amountWithTax the new amount with tax
     */
    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }
}