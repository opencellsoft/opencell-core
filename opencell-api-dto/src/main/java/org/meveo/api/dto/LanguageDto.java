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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.Language;
import org.meveo.model.billing.TradingLanguage;

import java.util.List;

/**
 * The Class LanguageDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "Language")
@XmlAccessorType(XmlAccessType.FIELD)
public class LanguageDto extends AuditableEntityDto implements IEnableDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 725968016559888810L;

    /**
     * Language code
     */
    @XmlAttribute(required = true)
    private String code;

    /**
     * Description
     */
    private String description;

    /**
     * Is entity disabled. Value is ignored in Update action - use enable/disable API instead.
     */
    private Boolean disabled;

    /** The language descriptions. */
    private List<LanguageDescriptionDto> languageDescriptions;

    /**
     * Instantiates a new language dto.
     */
    public LanguageDto() {

    }

    /**
     * Instantiates a new language dto.
     *
     * @param tradingLanguage the trading language
     */
    public LanguageDto(TradingLanguage tradingLanguage) {
        super(tradingLanguage);
        code = tradingLanguage.getLanguageCode();
        description = tradingLanguage.getPrDescription();
        disabled = tradingLanguage.isDisabled();
    }

    /**
     * Instantiates a new language dto.
     *
     * @param language the language
     */
    public LanguageDto(Language language) {
        super(language);
        code = language.getLanguageCode();
        description = language.getDescriptionEn();
        languageDescriptions = LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(language.getDescriptionI18n());
    }

    /**
     * Gets the code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     *
     * @param code the new code
     */
    public void setCode(String code) {
        this.code = code;
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
        return "LanguageDto [code=" + code + ", description=" + description + ", disabled=" + disabled + "]";
    }

    @Override
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public Boolean isDisabled() {
        return disabled;
    }

    public List<LanguageDescriptionDto> getLanguageDescriptions() {
        return languageDescriptions;
    }

    public void setLanguageDescriptions(List<LanguageDescriptionDto> languageDescriptions) {
        this.languageDescriptions = languageDescriptions;
    }
}