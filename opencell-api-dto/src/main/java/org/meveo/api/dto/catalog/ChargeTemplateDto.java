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

package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.model.BaseEntity;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.catalog.TriggeredEDRTemplate;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The Class ChargeTemplateDto.
 *
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "ChargeTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ChargeTemplateDto extends EnableBusinessDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5143285194077662656L;

    /** The invoice sub category. */ 
    @Schema(description = "The invoice sub category")
    private String invoiceSubCategory;

    /** The amount editable. */
    @Schema(description = "The amount can be editable")
    private Boolean amountEditable;

    /** The language descriptions. */
    @Schema(description = "list of the language description")
    private List<LanguageDescriptionDto> languageDescriptions;

    /** The input unit description. */
    @Deprecated
    private String inputUnitDescription;

    /** The rating unit description. */
    @Deprecated
    private String ratingUnitDescription;

    /** The unit multiplicator. */
    @Deprecated
    private BigDecimal unitMultiplicator;

    @Schema(description = "code of unit  measure")
    private String inputUnitOfMeasureCode;

    @Schema(description = "code of rating unit of measure")
    private String ratingUnitOfMeasureCode;

    @Schema(description = "input unit expression language")
    private String inputUnitEL;

    @Schema(description = "output unit expression language")
    private String outputUnitEL;

    /**
     * EDR and WO quantity field value precision
     */
    @Schema(description = "EDR and WO quantity field value precision")
    private Integer unitNbDecimal = BaseEntity.NB_DECIMALS;

    /**
     * EDR and WO quantity field value rounding
     */
    @Schema(description = "EDR and WO quantity field value rounding", example = "possible value are : NEAREST, DOWN, UP")
    private RoundingModeEnum roundingModeDtoEnum;

    /** The revenue recognition rule code. */
    @Schema(description = "The revenue recognition rule code")
    private String revenueRecognitionRuleCode;

    /** The filter expression. */
    @Size(max = 2000)
    @Schema(description = "The filter expression")
    private String filterExpression = null;

    /** The filter expression. */
    @Size(max = 2000)
    @Schema(description = "The filter expression for spark")
    private String filterExpressionSpark;

    /**
     * Charge tax class code
     **/
    @Schema(description = "code of tax class")
    private String taxClassCode;

    /**
     * Expression to determine tax class
     */
    @Schema(description = "Expression to determine tax class")
    private String taxClassEl;

    /**
     * Expression to determine tax class - for Spark
     */
    @Schema(description = "Expression to determine tax class - for Spark")
    private String taxClassElSpark;

    /**
     * Code of a rating script
     */
    @Schema(description = "Code of a rating script")
    private String ratingScriptCode;

    /**
     * The custom fields.
     */
    @Schema(description = "The custom fields")
    private CustomFieldsDto customFields;

    /**
     * The triggered edrs.
     */
    @Schema(description = "The triggered edrs")
    private TriggeredEdrTemplatesDto triggeredEdrs = new TriggeredEdrTemplatesDto();

    /**
     * Enable/disable removing WO rated to 0.
     */
    @Schema(description = "Enable/disable removing WO rated to 0")
    private boolean dropZeroWo;

    /**
     * Sorting index EL.
     */
    @Size(max = 2000)
    @Schema(description = "Sorting index EL")
    private String sortIndexEl = null;

    /**
     * Instantiates a new charge template dto.
     */
    public ChargeTemplateDto() {

    }

    /**
     * Instantiates a new charge template dto.
     *
     * @param chargeTemplate the charge template
     * @param customFieldInstances the custom field instances
     */
    public ChargeTemplateDto(ChargeTemplate chargeTemplate, CustomFieldsDto customFieldInstances) {
        super(chargeTemplate);
        if (chargeTemplate.getInvoiceSubCategory() != null) {
            invoiceSubCategory = chargeTemplate.getInvoiceSubCategory().getCode();
        }
        amountEditable = chargeTemplate.getAmountEditable();
        if (chargeTemplate.getEdrTemplates() != null) {
            triggeredEdrs = new TriggeredEdrTemplatesDto();
            for (TriggeredEDRTemplate edrTemplate : chargeTemplate.getEdrTemplates()) {
                triggeredEdrs.getTriggeredEdr().add(new TriggeredEdrTemplateDto(edrTemplate));
            }
        }
        roundingModeDtoEnum = chargeTemplate.getRoundingMode();
        customFields = customFieldInstances;
        inputUnitOfMeasureCode = chargeTemplate.getInputUnitOfMeasure() != null ? chargeTemplate.getInputUnitOfMeasure().getCode() : null;
        ratingUnitOfMeasureCode = chargeTemplate.getRatingUnitOfMeasure() != null ? chargeTemplate.getRatingUnitOfMeasure().getCode() : null;
        inputUnitEL = chargeTemplate.getInputUnitEL();
        outputUnitEL = chargeTemplate.getOutputUnitEL();
        inputUnitDescription = chargeTemplate.getInputUnitDescription();
        ratingUnitDescription = chargeTemplate.getRatingUnitDescription();
        unitMultiplicator = chargeTemplate.getUnitMultiplicator();
        unitNbDecimal = chargeTemplate.getUnitNbDecimal();
        roundingModeDtoEnum = chargeTemplate.getRoundingMode();
        revenueRecognitionRuleCode = chargeTemplate.getRevenueRecognitionRule() == null ? null : chargeTemplate.getRevenueRecognitionRule().getCode();
        setLanguageDescriptions(LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(chargeTemplate.getDescriptionI18n()));
        if (chargeTemplate.getTaxClass() != null) {
            taxClassCode = chargeTemplate.getTaxClass().getCode();
        }
        taxClassEl = chargeTemplate.getTaxClassEl();
        taxClassElSpark = chargeTemplate.getTaxClassElSpark();

        filterExpression = chargeTemplate.getFilterExpression();
        filterExpressionSpark = chargeTemplate.getFilterExpressionSpark();

        if (chargeTemplate.getRatingScript() != null) {
            ratingScriptCode = chargeTemplate.getRatingScript().getCode();
        }
        dropZeroWo = chargeTemplate.isDropZeroWo();
        sortIndexEl = chargeTemplate.getSortIndexEl();
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
     * Gets the amount editable.
     *
     * @return the amount editable
     */
    public Boolean getAmountEditable() {
        return amountEditable;
    }

    /**
     * Sets the amount editable.
     *
     * @param amountEditable the new amount editable
     */
    public void setAmountEditable(Boolean amountEditable) {
        this.amountEditable = amountEditable;
    }

    /**
     * Gets the invoice sub category.
     *
     * @return the invoice sub category
     */
    public String getInvoiceSubCategory() {
        return invoiceSubCategory;
    }

    /**
     * Sets the invoice sub category.
     *
     * @param invoiceSubCategory the new invoice sub category
     */
    public void setInvoiceSubCategory(String invoiceSubCategory) {
        this.invoiceSubCategory = invoiceSubCategory;
    }

    /**
     * Gets the input unit description.
     *
     * @return the input unit description
     */
    public String getInputUnitDescription() {
        return inputUnitDescription;
    }

    /**
     * Sets the input unit description.
     *
     * @param inputUnitDescription the new input unit description
     */
    public void setInputUnitDescription(String inputUnitDescription) {
        this.inputUnitDescription = inputUnitDescription;
    }

    /**
     * Gets the rating unit description.
     *
     * @return the rating unit description
     */
    public String getRatingUnitDescription() {
        return ratingUnitDescription;
    }

    /**
     * Sets the rating unit description.
     *
     * @param ratingUnitDescription the new rating unit description
     */
    public void setRatingUnitDescription(String ratingUnitDescription) {
        this.ratingUnitDescription = ratingUnitDescription;
    }

    /**
     * Gets the unit multiplicator.
     *
     * @return the unit multiplicator
     */
    public BigDecimal getUnitMultiplicator() {
        return unitMultiplicator;
    }

    /**
     * Sets the unit multiplicator.
     *
     * @param unitMultiplicator the new unit multiplicator
     */
    public void setUnitMultiplicator(BigDecimal unitMultiplicator) {
        this.unitMultiplicator = unitMultiplicator;
    }

    /**
     * Gets the unit nb decimal.
     *
     * @return the unit nb decimal
     */
    public Integer getUnitNbDecimal() {
        return unitNbDecimal;
    }

    /**
     * Sets the unit nb decimal.
     *
     * @param unitNbDecimal the new unit nb decimal
     */
    public void setUnitNbDecimal(Integer unitNbDecimal) {
        this.unitNbDecimal = unitNbDecimal;
    }

    /**
     * Gets the triggered edrs.
     *
     * @return the triggered edrs
     */
    public TriggeredEdrTemplatesDto getTriggeredEdrs() {
        return triggeredEdrs;
    }

    /**
     * Sets the triggered edrs.
     *
     * @param triggeredEdrs the new triggered edrs
     */
    public void setTriggeredEdrs(TriggeredEdrTemplatesDto triggeredEdrs) {
        this.triggeredEdrs = triggeredEdrs;
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

    /**
     * Gets the rounding mode dto enum.
     *
     * @return the rounding mode dto enum
     */
    public RoundingModeEnum getRoundingModeDtoEnum() {
        return roundingModeDtoEnum;
    }

    /**
     * Sets the rounding mode dto enum.
     *
     * @param roundingModeDtoEnum the new rounding mode dto enum
     */
    public void setRoundingModeDtoEnum(RoundingModeEnum roundingModeDtoEnum) {
        this.roundingModeDtoEnum = roundingModeDtoEnum;
    }

    /**
     * Gets the revenue recognition rule code.
     *
     * @return the revenue recognition rule code
     */
    public String getRevenueRecognitionRuleCode() {
        return revenueRecognitionRuleCode;
    }

    /**
     * Sets the revenue recognition rule code.
     *
     * @param revenueRecognitionRuleCode the new revenue recognition rule code
     */
    public void setRevenueRecognitionRuleCode(String revenueRecognitionRuleCode) {
        this.revenueRecognitionRuleCode = revenueRecognitionRuleCode;
    }

    @Override
    public String toString() {
        return "code=" + getCode() + ", description=" + getDescription() + ", invoiceSubCategory=" + invoiceSubCategory + ", disabled=" + isDisabled() + ", amountEditable=" + amountEditable + ", languageDescriptions="
                + languageDescriptions + ", inputUnitDescription=" + inputUnitDescription + ", ratingUnitDescription=" + ratingUnitDescription + ", unitMultiplicator=" + unitMultiplicator + ", unitNbDecimal="
                + unitNbDecimal + ", customFields=" + customFields + ", triggeredEdrs=" + triggeredEdrs + ",roundingModeDtoEnum=" + roundingModeDtoEnum + ", filterExpression=" + filterExpression
                + ", filterExpressionSpark=" + filterExpressionSpark;
    }

    /**
     * @return the inputUnitOfMeasureCode
     */
    public String getInputUnitOfMeasureCode() {
        return inputUnitOfMeasureCode;
    }

    /**
     * @param inputUnitOfMeasureCode the inputUnitOfMeasureCode to set
     */
    public void setInputUnitOfMeasureCode(String inputUnitOfMeasureCode) {
        this.inputUnitOfMeasureCode = inputUnitOfMeasureCode;
    }

    /**
     * @return the ratingUnitOfMeasureCode
     */
    public String getRatingUnitOfMeasureCode() {
        return ratingUnitOfMeasureCode;
    }

    /**
     * @param ratingUnitOfMeasureCode the ratingUnitOfMeasureCode to set
     */
    public void setRatingUnitOfMeasureCode(String ratingUnitOfMeasureCode) {
        this.ratingUnitOfMeasureCode = ratingUnitOfMeasureCode;
    }

    /**
     * @return the inputUnitEL
     */
    public String getInputUnitEL() {
        return inputUnitEL;
    }

    /**
     * @param inputUnitEL the inputUnitEL to set
     */
    public void setInputUnitEL(String inputUnitEL) {
        this.inputUnitEL = inputUnitEL;
    }

    /**
     * @return the outputUnitEL
     */
    public String getOutputUnitEL() {
        return outputUnitEL;
    }

    /**
     * @param outputUnitEL the outputUnitEL to set
     */
    public void setOutputUnitEL(String outputUnitEL) {
        this.outputUnitEL = outputUnitEL;
    }

    /**
     * @return Charge tax class code
     */
    public String getTaxClassCode() {
        return taxClassCode;
    }

    /**
     * @param taxClassCode Charge tax class code
     */
    public void setTaxClassCode(String taxClassCode) {
        this.taxClassCode = taxClassCode;
    }

    /**
     * @return Expression to determine tax class
     */
    public String getTaxClassEl() {
        return taxClassEl;
    }

    /**
     * @param taxClassEl Expression to determine tax class
     */
    public void setTaxClassEl(String taxClassEl) {
        this.taxClassEl = taxClassEl;
    }

    /**
     * @return Expression to determine tax class - for Spark
     */
    public String getTaxClassElSpark() {
        return taxClassElSpark;
    }

    /**
     * @param taxClassElSpark Expression to determine tax class - for Spark
     */
    public void setTaxClassElSpark(String taxClassElSpark) {
        this.taxClassElSpark = taxClassElSpark;
    }

    /**
     * @return Expression to determine if charge applies
     */
    public String getFilterExpression() {
        return filterExpression;
    }

    /**
     * @param filterExpression Expression to determine if charge applies
     */
    public void setFilterExpression(String filterExpression) {
        this.filterExpression = filterExpression;
    }

    /**
     * @return Expression to determine if charge applies - for Spark
     */
    public String getFilterExpressionSpark() {
        return filterExpressionSpark;
    }

    /**
     * @param filterExpressionSpark Expression to determine if charge applies - for Spark
     */
    public void setFilterExpressionSpark(String filterExpressionSpark) {
        this.filterExpressionSpark = filterExpressionSpark;
    }

    /**
     * @return Code of a rating script
     */
    public String getRatingScriptCode() {
        return ratingScriptCode;
    }

    /**
     * @param ratingScriptCode Code of a rating script
     */
    public void setRatingScriptCode(String ratingScriptCode) {
        this.ratingScriptCode = ratingScriptCode;
    }
    /**
     * Check if removing WO rated to 0 is enabled or not.
     *
     * @return true if is enabled false else.
     */
    public boolean isDropZeroWo() {
        return dropZeroWo;
    }

    /**
     * Enable/disable removing WO rated to 0.
     *
     * @param dropZeroWo
     */
    public void setDropZeroWo(boolean dropZeroWo) {
        this.dropZeroWo = dropZeroWo;
    }

    /**
     * Gets sorting index EL.
     *
     * @return an EL expression for sorting index
     */
    public String getSortIndexEl() {
        return sortIndexEl;
    }

    /**
     * Sets sorting index EL.
     *
     * @param sortIndexEl El expression for sorting index
     */
    public void setSortIndexEl(String sortIndexEl) {
        this.sortIndexEl = sortIndexEl;
    }
}