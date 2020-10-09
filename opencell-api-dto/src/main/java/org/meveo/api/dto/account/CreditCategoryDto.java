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
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.model.payments.CreditCategory;

import java.util.List;

/**
 * The Class CreditCategoryDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "CreditCategory")
@XmlAccessorType(XmlAccessType.FIELD)
public class CreditCategoryDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 9096295121437014513L;

    private List<LanguageDescriptionDto> languageDescriptions;

    /**
     * Instantiates a new credit category dto.
     */
    public CreditCategoryDto() {

    }

    /**
     * Instantiates a new credit category dto.
     *
     * @param creditCategory the CreditCategory entity
     */
    public CreditCategoryDto(CreditCategory creditCategory) {
        super(creditCategory);
        languageDescriptions = convertMultiLanguageFromMapOfValues(creditCategory.getDescriptionI18n());
    }

    @Override
    public String toString() {
        return "CreditCategoryDto [code=" + getCode() + ", description=" + getDescription() + "]";
    }

    public List<LanguageDescriptionDto> getLanguageDescriptions() {
        return languageDescriptions;
    }

    public void setLanguageDescriptions(List<LanguageDescriptionDto> languageDescriptions) {
        this.languageDescriptions = languageDescriptions;
    }
}