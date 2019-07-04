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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;

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
    private String categoryInvoiceCode;

    /** The description */
    private String description;

    /** The user account code */
    private String userAccountCode;

    /** The item number */
    private Integer itemNumber;

    /** The amount without tax */
    private BigDecimal amountWithoutTax;

    /** The amount tax */
    private BigDecimal amountTax;

    /** The amount with tax */
    private BigDecimal amountWithTax;

    /**
     * Sub category invoice aggregates
     */
    @XmlElementWrapper
    @XmlElement(name = "subCategoryInvoiceAgregateDto", required = true)
    private List<SubCategoryInvoiceAgregateDto> listSubCategoryInvoiceAgregateDto = new ArrayList<SubCategoryInvoiceAgregateDto>();

    /**
     * Discount aggregates
     */
    @XmlElementWrapper
    @XmlElement(name = "discountAggregate")
    private List<DiscountInvoiceAggregateDto> discountAggregates;

    /**
     * Instantiates a new category invoice aggregate dto
     * 
     * @param categoryAggregate Category invoice aggregate
     * @param includeTransactions Should Rated transactions be detailed in subcategory aggregate level
     */
    public CategoryInvoiceAgregateDto(CategoryInvoiceAgregate categoryAggregate, boolean includeTransactions) {

        this.categoryInvoiceCode = categoryAggregate.getInvoiceCategory().getCode();
        this.description = categoryAggregate.getDescription();
        this.amountWithoutTax = categoryAggregate.getAmountWithoutTax();
        this.amountWithTax = categoryAggregate.getAmountWithTax();
        this.amountTax = categoryAggregate.getAmountTax();
        this.itemNumber = categoryAggregate.getItemNumber();
        if (categoryAggregate.getUserAccount() != null) {
            this.userAccountCode = categoryAggregate.getUserAccount().getCode();
        }
        for (SubCategoryInvoiceAgregate subCategoryAggregate : categoryAggregate.getSubCategoryInvoiceAgregates()) {

            if (subCategoryAggregate.isDiscountAggregate()) {
                if (discountAggregates == null) {
                    discountAggregates = new ArrayList<>();
                }
                discountAggregates.add(new DiscountInvoiceAggregateDto(subCategoryAggregate));
            } else {
                listSubCategoryInvoiceAgregateDto.add(new SubCategoryInvoiceAgregateDto(subCategoryAggregate, includeTransactions));
            }
        }

        listSubCategoryInvoiceAgregateDto.sort(Comparator.comparing(SubCategoryInvoiceAgregateDto::getInvoiceSubCategoryCode));
        if (discountAggregates != null) {
            discountAggregates.sort(Comparator.comparing(DiscountInvoiceAggregateDto::getDiscountPlanItemCode));
        }
    }

    /**
     * Instantiates a new category invoice aggregate dto.
     */
    public CategoryInvoiceAgregateDto() {
    }

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