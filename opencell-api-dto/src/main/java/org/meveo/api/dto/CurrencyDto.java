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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.admin.Currency;
import org.meveo.model.billing.TradingCurrency;

import java.util.List;

/**
 * The Class CurrencyDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "Currency")
@XmlAccessorType(XmlAccessType.FIELD)
public class CurrencyDto extends AuditableEntityDto implements IEnableDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 9143645109603442839L;

    /**
     * Currency code
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
    
    @Deprecated
    private BigDecimal prCurrencyToThis;

    /** The language descriptions. */
    private List<LanguageDescriptionDto> languageDescriptions;

    /**
     * Instantiates a new currency dto.
     */
    public CurrencyDto() {

    }

    /**
     * Instantiates a new currency dto.
     *
     * @param tradingCurrency the trading currency
     */
    public CurrencyDto(TradingCurrency tradingCurrency) {
        super(tradingCurrency);
        code = tradingCurrency.getCurrencyCode();
        description = tradingCurrency.getPrDescription();
        disabled = tradingCurrency.isDisabled();
        prCurrencyToThis = tradingCurrency.getPrCurrencyToThis();
    }

    /**
     * Instantiates a new currency dto.
     *
     * @param currency the currency
     */
    public CurrencyDto(Currency currency) {
        code = currency.getCurrencyCode();
        description = currency.getDescriptionEn();
        languageDescriptions = LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(currency.getDescriptionI18n());
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
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public Boolean isDisabled() {
        return disabled;
    }
    
    /**
     * @return the prCurrencyToThis
     */
    public BigDecimal getPrCurrencyToThis() {
        return prCurrencyToThis;
    }

    /**
     * @param prCurrencyToThis the prCurrencyToThis to set
     */
    public void setPrCurrencyToThis(BigDecimal prCurrencyToThis) {
        this.prCurrencyToThis = prCurrencyToThis;
    }

    @Override
    public String toString() {
        return "CurrencyDto [code=" + code + ", description=" + description + ", disabled=" + disabled + "]";
    }

    public List<LanguageDescriptionDto> getLanguageDescriptions() {
        return languageDescriptions;
    }

    public void setLanguageDescriptions(List<LanguageDescriptionDto> languageDescriptions) {
        this.languageDescriptions = languageDescriptions;
    }
}