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

package org.meveo.api.dto.tax;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.v3.oas.annotations.media.Schema;
import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.model.tax.TaxCategory;

/**
 * DTO implementation of Tax category. Tax category
 * 
 * @author Andrius Karpavicius
 *
 */
@XmlRootElement(name = "TaxCategory")
@XmlAccessorType(XmlAccessType.FIELD)
public class TaxCategoryDto extends BusinessEntityDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Translated descriptions in JSON format with language code as a key and translated description as a value
     **/
    protected List<LanguageDescriptionDto> descriptionI18n;

    /** The custom fields. */
    private CustomFieldsDto customFields;

    @XmlElement(required = true)
    @Schema(description = "The UntdidTaxationCategory")
    protected String untdidTaxationCategoryCode;

    /**
     * Default constructor
     */
    public TaxCategoryDto() {
        super();
    }

    /**
     * Instantiates a new TaxCategory Dto.
     *
     * @param entity The Tax category entity
     * @param customFieldInstances the custom field instances
     */
    public TaxCategoryDto(TaxCategory entity, CustomFieldsDto customFieldInstances) {
        super(entity);

        customFields = customFieldInstances;

        if (entity.getDescriptionI18n() != null) {
            this.descriptionI18n = LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(entity.getDescriptionI18n());
        }
    }

    /**
     * @return descriptionI18n Translated descriptions in JSON format with language code as a key and translated description as a value
     */
    public List<LanguageDescriptionDto> getDescriptionI18n() {
        return this.descriptionI18n;
    }

    /**
     * @param descriptionI18n Translated descriptions in JSON format with language code as a key and translated description as a value
     */
    public void setDescriptionI18n(List<LanguageDescriptionDto> descriptionI18n) {
        this.descriptionI18n = descriptionI18n;
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

    public String getUntdidTaxationCategoryCode() {
        return untdidTaxationCategoryCode;
    }

    public void setUntdidTaxationCategoryCode(String untdidTaxationCategoryCode) {
        this.untdidTaxationCategoryCode = untdidTaxationCategoryCode;
    }
}