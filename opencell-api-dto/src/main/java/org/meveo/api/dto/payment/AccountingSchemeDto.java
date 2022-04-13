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

package org.meveo.api.dto.payment;

import java.util.List;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.v3.oas.annotations.media.Schema;
import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.model.payments.AccountingScheme;
import org.meveo.model.scripts.ScriptInstance;

/**
 * The Class AccountingSchemeDto.
 *
 * @author Mohamed STITANE
 * @since 13.0.0
 */
@XmlRootElement(name = "AccountingScheme")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountingSchemeDto extends BusinessEntityDto {

    /**
     * The longDescription.
     */
    @Schema(description = "a long description")
    private String longDescription;

    /**
     * The longDescription I18N.
     */
    @Schema(description = "i18n a long description")
    private List<LanguageDescriptionDto> longDescriptionsTranslated;

    /**
     * The scriptCode.
     */
    @Schema(description = "the script code", required = true)
    private String scriptCode;

    public AccountingSchemeDto(){}

    public AccountingSchemeDto(AccountingScheme accountingScheme) {
        code = accountingScheme.getCode();
        description = accountingScheme.getDescription();
        scriptCode = Optional.ofNullable(accountingScheme.getScriptInstance()).map(ScriptInstance::getCode).orElse(null);
        longDescriptionsTranslated = LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(accountingScheme.getLongDescriptionI18n());
    }

    public String getScriptCode() {
        return scriptCode;
    }

    public void setScriptCode(String scriptCode) {
        this.scriptCode = scriptCode;
    }

    public List<LanguageDescriptionDto> getLongDescriptionsTranslated() {
        return longDescriptionsTranslated;
    }

    public void setLongDescriptionsTranslated(List<LanguageDescriptionDto> longDescriptionsTranslated) {
        this.longDescriptionsTranslated = longDescriptionsTranslated;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }
}