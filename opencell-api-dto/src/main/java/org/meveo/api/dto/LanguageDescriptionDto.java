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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The Class LanguageDescriptionDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "LanguageDescription")
@XmlAccessorType(XmlAccessType.FIELD)
public class LanguageDescriptionDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4686792860854718893L;

    /** The language code. */
    @Schema(description = "The language code")
    private String languageCode;

    /** The description. */
    @Schema(description = "The description")
    private String description;

    /**
     * Instantiates a new language description dto.
     */
    public LanguageDescriptionDto() {

    }

    /**
     * Instantiates a new language description dto.
     *
     * @param languageCode the language code
     * @param description the description
     */
    public LanguageDescriptionDto(String languageCode, String description) {
        this.languageCode = languageCode;
        this.description = description;
    }

    /**
     * Gets the language code.
     *
     * @return the language code
     */
    public String getLanguageCode() {
        return languageCode;
    }

    /**
     * Sets the language code.
     *
     * @param languageCode the new language code
     */
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
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
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "LanguageDescriptionDto [languageCode=" + languageCode + ", description=" + description + "]";
    }

    /**
     * Convert multi language field value map into a list of multi language field DTO values.
     *
     * @param multiLanguageMap Map of values with language code as a key
     * @return Multi langauge field DTO values
     */
    public static List<LanguageDescriptionDto> convertMultiLanguageFromMapOfValues(Map<String, String> multiLanguageMap) {

        if (multiLanguageMap == null || multiLanguageMap.isEmpty()) {
            return null;
        }

        List<LanguageDescriptionDto> translationInfos = new ArrayList<>();

        for (Entry<String, String> translationInfo : multiLanguageMap.entrySet()) {
            if (translationInfo.getValue() != null) {
                translationInfos.add(new LanguageDescriptionDto(translationInfo.getKey(), translationInfo.getValue()));
            }
        }

        return translationInfos;
    }
}