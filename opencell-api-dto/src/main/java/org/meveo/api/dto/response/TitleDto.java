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

package org.meveo.api.dto.response;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.model.shared.Title;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The Class TitleDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "Title")
@XmlAccessorType(XmlAccessType.FIELD)
public class TitleDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1332916104721562522L;

    /** The is company. */
    @Schema(description = "indicate if the title is a  company", defaultValue = "false")
    private Boolean isCompany = Boolean.FALSE;

    /** The language descriptions. */
    @Schema(description = "list of the language description")
    private List<LanguageDescriptionDto> languageDescriptions;

    /**
     * Instantiates a new title dto.
     */
    public TitleDto() {

    }

    /**
     * Instantiates a new title dto.
     *
     * @param title the title entity
     * @param customFieldInstances Custom field values. Not applicable here.
     */
    public TitleDto(Title title, CustomFieldsDto customFieldInstances) {
        super(title);
        isCompany = title.getIsCompany();
        languageDescriptions = LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(title.getDescriptionI18n());
    }

    /**
     * Gets the checks if is company.
     *
     * @return the checks if is company
     */
    public Boolean getIsCompany() {
        return isCompany;
    }

    /**
     * Sets the checks if is company.
     *
     * @param isCompany the new checks if is company
     */
    public void setIsCompany(Boolean isCompany) {
        this.isCompany = isCompany;
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

    @Override
    public String toString() {
        return "TitleDto [code=" + getCode() + ", description=" + getDescription() + ", isCompany=" + isCompany + "]";
    }
}