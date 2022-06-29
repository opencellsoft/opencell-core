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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.v3.oas.annotations.media.Schema;
import org.meveo.model.billing.InvoiceCategory;

/**
 * The Class InvoiceCategoryDto.
 *
 * @author Edward P. Legaspi
 * 
 * @lastModifiedVersion 5.1
 */
@XmlRootElement(name = "InvoiceCategory")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceCategoryDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5166093858617578774L;

    /** The language descriptions. */
    @Schema(description = "Description of languages.")
    private List<LanguageDescriptionDto> languageDescriptions;

    /**
     * The occ template code.
     */
    @XmlElement(required = true)
    @Schema(description = "The occ template code.")
    private String occTemplateCode;

    /**
     * The occ template negative code.
     */
    @Schema(description = "The occ template negative code.")
    private String occTemplateNegativeCode;

    /**
     * The custom fields.
     */
    @Schema(description = "The custom fields.")
    private CustomFieldsDto customFields;

    /**
     * Sorting index.
     */
    @Schema(description = "Sorting index.")
    private Integer sortIndex;

    /**
     * Instantiates a new invoice category dto.
     */
    public InvoiceCategoryDto() {

    }

    /**
     * Instantiates a new invoice category dto.
     *
     * @param invoiceCategory the invoice category
     * @param customFieldInstances the custom field instances
     */
    public InvoiceCategoryDto(InvoiceCategory invoiceCategory, CustomFieldsDto customFieldInstances) {
        super(invoiceCategory);
        customFields = customFieldInstances;
        setLanguageDescriptions(LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(invoiceCategory.getDescriptionI18n()));
        if(invoiceCategory.getOccTemplate() != null) {
            setOccTemplateCode(invoiceCategory.getOccTemplate().getCode());
        }
        if(invoiceCategory.getOccTemplateNegative() != null) {
            setOccTemplateNegativeCode(invoiceCategory.getOccTemplateNegative().getCode());
        }
        sortIndex = invoiceCategory.getSortIndex();
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
     * @return the customFields
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * Sets the custom fields.
     *
     * @param customFields the customFields to set
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }
    
    

    /**
     * @return the occTemplateCode
     */
    public String getOccTemplateCode() {
        return occTemplateCode;
    }

    /**
     * @param occTemplateCode the occTemplateCode to set
     */
    public void setOccTemplateCode(String occTemplateCode) {
        this.occTemplateCode = occTemplateCode;
    }

    /**
     * @return the occTemplateNegativeCode
     */
    public String getOccTemplateNegativeCode() {
        return occTemplateNegativeCode;
    }

    /**
     * @param occTemplateNegativeCode the occTemplateNegativeCode to set
     */
    public void setOccTemplateNegativeCode(String occTemplateNegativeCode) {
        this.occTemplateNegativeCode = occTemplateNegativeCode;
    }

    /**
     * Gets the sorting index.
     *
     * @return the sorting index
     */
    public Integer getSortIndex() {
        return sortIndex;
    }

    /**
     * Sets the sorting index.
     *
     * @param sortIndex the sorting index.
     */
    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }

    @Override
    public String toString() {
        return "InvoiceCategoryDto [code=" + getCode() + ", description=" + getDescription() + ", languageDescriptions=" + languageDescriptions + ", occTemplateCode="
                + occTemplateCode + ", occTemplateNegativeCode=" + occTemplateNegativeCode + ", customFields=" + customFields
                + "]";
    }

}
