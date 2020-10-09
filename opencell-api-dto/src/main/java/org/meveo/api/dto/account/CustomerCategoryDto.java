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

import static org.meveo.api.dto.LanguageDescriptionDto.convertMultiLanguageFromMapOfValues;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.model.crm.CustomerCategory;

import java.util.List;

/**
 * The Class CustomerCategoryDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "CustomerCategory")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerCategoryDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8778571285967620018L;

    /** The exonerated from taxes. */
    private Boolean exoneratedFromTaxes;

    /** The exoneration tax el. */
    private String exonerationTaxEl;

    /** The exoneration tax el for Spark. */
    private String exonerationTaxElSpark;

    /** The exoneration reason. */
    private String exonerationReason;

    /** The accounting code. */
    @XmlElement(required = true)
    private String accountingCode;

    /**
     * Account tax category - code
     **/
    private String taxCategoryCode;

    /**
     * Expression to determine tax category code
     */
    private String taxCategoryEl;

    /**
     * Expression to determine tax category code - for Spark
     */
    private String taxCategoryElSpark;

    private List<LanguageDescriptionDto> languageDescriptions;

    /**
     * Instantiates a new customer category dto.
     */
    public CustomerCategoryDto() {

    }

    /**
     * Instantiates a new customer category dto.
     *
     * @param customerCategory the customerCategory entity
     */
    public CustomerCategoryDto(CustomerCategory customerCategory) {
        super(customerCategory);
        exoneratedFromTaxes = customerCategory.getExoneratedFromTaxes();
        exonerationReason = customerCategory.getExonerationReason();
        exonerationTaxEl = customerCategory.getExonerationTaxEl();
        exonerationTaxElSpark = customerCategory.getExonerationTaxElSpark();
        languageDescriptions = convertMultiLanguageFromMapOfValues(customerCategory.getDescriptionI18n());
        if (customerCategory.getAccountingCode() != null) {
            accountingCode = customerCategory.getAccountingCode().getCode();
        }
        if (customerCategory.getTaxCategory() != null) {
            taxCategoryCode = customerCategory.getTaxCategory().getCode();
        }
        taxCategoryEl = customerCategory.getTaxCategoryEl();
        taxCategoryElSpark = customerCategory.getTaxCategoryElSpark();
    }

    /**
     * @return True if account is exonerated from taxes
     */
    public Boolean isExoneratedFromTaxes() {
        return exoneratedFromTaxes;
    }

    /**
     * @param exoneratedFromTaxes True if account is exonerated from taxes
     */
    public void setExoneratedFromTaxes(Boolean exoneratedFromTaxes) {
        this.exoneratedFromTaxes = exoneratedFromTaxes;
    }

    /**
     * @return Expression to determine if account is exonerated from taxes
     */
    public String getExonerationTaxEl() {
        return exonerationTaxEl;
    }

    /**
     * @param exonerationTaxEl Expression to determine if account is exonerated from taxes
     */
    public void setExonerationTaxEl(String exonerationTaxEl) {
        this.exonerationTaxEl = exonerationTaxEl;
    }

    /**
     * @return Expression to determine if account is exonerated from taxes - for Spark
     */
    public String getExonerationTaxElSpark() {
        return exonerationTaxElSpark;
    }

    /**
     * @param exonerationTaxElSpark Expression to determine if account is exonerated from taxes - for Spark
     */
    public void setExonerationTaxElSpark(String exonerationTaxElSpark) {
        this.exonerationTaxElSpark = exonerationTaxElSpark;
    }

    /**
     * @return the exonerationReason
     */
    public String getExonerationReason() {
        return exonerationReason;
    }

    /**
     * @param exonerationReason the exonerationReason to set
     */
    public void setExonerationReason(String exonerationReason) {
        this.exonerationReason = exonerationReason;
    }

    /**
     * @return the accounting code
     */
    public String getAccountingCode() {
        return accountingCode;
    }

    /**
     * @param accountingCode the accounting code to set
     */
    public void setAccountingCode(String accountingCode) {
        this.accountingCode = accountingCode;
    }

    /**
     * @return Account tax category - code
     */
    public String getTaxCategoryCode() {
        return taxCategoryCode;
    }

    /**
     * @param taxCategory Account tax category - code
     */
    public void setTaxCategoryCode(String taxCategoryCode) {
        this.taxCategoryCode = taxCategoryCode;
    }

    /**
     * @return Expression to determine tax category code
     */
    public String getTaxCategoryEl() {
        return taxCategoryEl;
    }

    /**
     * @param taxCategoryEl Expression to determine tax category code
     */
    public void setTaxCategoryEl(String taxCategoryEl) {
        this.taxCategoryEl = taxCategoryEl;
    }

    /**
     * @return Expression to determine tax category code - for Spark
     */
    public String getTaxCategoryElSpark() {
        return taxCategoryElSpark;
    }

    /**
     * @param taxCategorySpark Expression to determine tax category code - for Spark
     */
    public void setTaxCategoryElSpark(String taxCategoryElSpark) {
        this.taxCategoryElSpark = taxCategoryElSpark;
    }

    @Override
    public String toString() {
        return "CustomerCategoryDto [exoneratedFromTaxes=" + exoneratedFromTaxes + ", exonerationTaxEl=" + exonerationTaxEl + ", exonerationTaxElSpark=" + exonerationTaxElSpark + ", exonerationReason="
                + exonerationReason + ", accountingCode=" + accountingCode + "]";
    }

    public List<LanguageDescriptionDto> getLanguageDescriptions() {
        return languageDescriptions;
    }

    public void setLanguageDescriptions(List<LanguageDescriptionDto> languageDescriptions) {
        this.languageDescriptions = languageDescriptions;
    }
}