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
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.Tax;

/**
 * DTO for {@link Tax}.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 **/
@XmlRootElement(name = "Tax")
@XmlAccessorType(XmlAccessType.FIELD)
public class TaxDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5184602572648722134L;

    /** The percent. */
    @XmlElement(required = true)
    private BigDecimal percent;

    /** The accounting code. */
    private String accountingCode;

    /** The language descriptions. */
    private List<LanguageDescriptionDto> languageDescriptions;

    /** The custom fields. */
    private CustomFieldsDto customFields;

    /**
     * Instantiates a new tax dto.
     */
    public TaxDto() {

    }

    /**
     * Instantiates a new tax dto.
     *
     * @param tax the tax
     * @param customFieldInstances the custom field instances
     * @param isShort If true only shot version of tax will be set
     */
    public TaxDto(Tax tax, CustomFieldsDto customFieldInstances, boolean isShort) {
        super(tax);
        percent = tax.getPercent();
        if (!isShort) {
            if (tax.getAccountingCode() != null) {
                accountingCode = tax.getAccountingCode().getCode();
            }
            customFields = customFieldInstances;
            setLanguageDescriptions(LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(tax.getDescriptionI18n()));

        } else {
            setAuditableFields(null);
            setAuditable((AuditableDto) null);
        }
    }

    /**
     * Gets the percent.
     *
     * @return the percent
     */
    public BigDecimal getPercent() {
        return percent;
    }

    /**
     * Sets the percent.
     *
     * @param percent the new percent
     */
    public void setPercent(BigDecimal percent) {
        this.percent = percent;
    }

    /**
     * Gets the accounting code.
     *
     * @return the accounting code
     */
    public String getAccountingCode() {
        return accountingCode;
    }

    /**
     * Sets the accounting code.
     *
     * @param accountingCode the new accounting code
     */
    public void setAccountingCode(String accountingCode) {
        this.accountingCode = accountingCode;
    }

    /**
     * Gets the language descriptions.
     *
     * @return the language descriptions
     */
    public List<LanguageDescriptionDto> getLanguageDescriptions() {
        return languageDescriptions;
    }

    /**
     * Sets the language descriptions.
     *
     * @param languageDescriptions the new language descriptions
     */
    public void setLanguageDescriptions(List<LanguageDescriptionDto> languageDescriptions) {
        this.languageDescriptions = languageDescriptions;
    }

    /**
     * Gets the custom fields.
     *
     * @return the custom fields
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * Sets the custom fields.
     *
     * @param customFields the new custom fields
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    @Override
    public String toString() {
        return "TaxDto [code=" + getCode() + ", description=" + getDescription() + ", percent=" + percent + ", accountingCode=" + accountingCode + ", languageDescriptions=" + languageDescriptions + ", customFields="
                + customFields + "]";
    }
}