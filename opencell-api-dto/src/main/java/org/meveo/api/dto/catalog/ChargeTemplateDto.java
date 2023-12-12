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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.model.BaseEntity;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.ChargeTemplateStatusEnum;
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

    @Schema(description = "ChargeTemplate status")
    private ChargeTemplateStatusEnum status;

    private List<String> linkedAttributes;

    @Schema(description = "Internal Note")
    private String internalNote;

    private Set<String> pricePlanCodes = new HashSet<>();
    
 // Parameter 1
    @Schema(description = "Description of Parameter 1")
    private String parameter1Description;

    @Schema(description = "Translated descriptions of Parameter 1")
    private List<LanguageDescriptionDto> parameter1TranslatedDescriptions;

    @Schema(description = "Translated long descriptions of Parameter 1")
    private List<LanguageDescriptionDto> parameter1TranslatedLongDescriptions;

    @Schema(description = "Format of Parameter 1")
    private ChargeTemplate.ParameterFormat parameter1Format;

    @Schema(description = "Is Parameter 1 Mandatory?")
    private Boolean parameter1IsMandatory;

    @Schema(description = "Is Parameter 1 Hidden?")
    private Boolean parameter1IsHidden;

    // Parameter 2
    @Schema(description = "Description of Parameter 2")
    private String parameter2Description;

    @Schema(description = "Translated descriptions of Parameter 2")
    private List<LanguageDescriptionDto> parameter2TranslatedDescriptions;

    @Schema(description = "Translated long descriptions of Parameter 2")
    private List<LanguageDescriptionDto> parameter2TranslatedLongDescriptions;

    @Schema(description = "Format of Parameter 2")
    private ChargeTemplate.ParameterFormat parameter2Format;

    @Schema(description = "Is Parameter 2 Mandatory?")
    private Boolean parameter2IsMandatory;

    @Schema(description = "Is Parameter 2 Hidden?")
    private Boolean parameter2IsHidden;

    // Parameter 3
    @Schema(description = "Description of Parameter 3")
    private String parameter3Description;

    @Schema(description = "Translated descriptions of Parameter 3")
    private List<LanguageDescriptionDto> parameter3TranslatedDescriptions;

    @Schema(description = "Translated long descriptions of Parameter 3")
    private List<LanguageDescriptionDto> parameter3TranslatedLongDescriptions;

    @Schema(description = "Format of Parameter 3")
    private ChargeTemplate.ParameterFormat parameter3Format;

    @Schema(description = "Is Parameter 3 Mandatory?")
    private Boolean parameter3IsMandatory;

    @Schema(description = "Is Parameter 3 Hidden?")
    private Boolean parameter3IsHidden;

    // Parameter Extra
    @Schema(description = "Description of Extra Parameter")
    private String parameterExtraDescription;

    @Schema(description = "Translated descriptions of Extra Parameter")
    private List<LanguageDescriptionDto> parameterExtraTranslatedDescriptions;

    @Schema(description = "Translated long descriptions of Extra Parameter")
    private List<LanguageDescriptionDto> parameterExtraTranslatedLongDescriptions;

    @Schema(description = "Format of Extra Parameter")
    private ChargeTemplate.ParameterFormat parameterExtraFormat;

    @Schema(description = "Is Extra Parameter Mandatory?")
    private Boolean parameterExtraIsMandatory;

    @Schema(description = "Is Extra Parameter Hidden?")
    private Boolean parameterExtraIsHidden;
    
    // Business Key
    @Schema(description = "Business Key El")
    private String businessKeyEl;
    
    @Schema(description = "Business Key Description")
    private String businessKeyDescription;

    @Schema(description = "Translated descriptions of Business Key")
    private List<LanguageDescriptionDto> businessKeyTranslatedDescriptions;

    @Schema(description = "Translated long descriptions of Business Key")
    private List<LanguageDescriptionDto> businessKeyTranslatedLongDescriptions;

    @Schema(description = "Format of Business Key")
    private ChargeTemplate.BusinessKeyFormat businessKeyFormat;

    @Schema(description = "Is Business Key Mandatory?")
    private Boolean businessKeyIsMandatory;

    @Schema(description = "Is Business Key Hidden?")
    private Boolean businessKeyIsHidden;

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
        filterExpression = chargeTemplate.getFilterExpression();

        if (chargeTemplate.getRatingScript() != null) {
            ratingScriptCode = chargeTemplate.getRatingScript().getCode();
        }
        if(chargeTemplate.getAttributes() != null && !chargeTemplate.getAttributes().isEmpty()){
            this.linkedAttributes = chargeTemplate.getAttributes()
                    .stream()
                    .map(att -> att.getCode())
                    .collect(Collectors.toList());
        }
        dropZeroWo = chargeTemplate.isDropZeroWo();
        sortIndexEl = chargeTemplate.getSortIndexEl();
        if(chargeTemplate.getStatus()!=null) {
        	status=chargeTemplate.getStatus();
        }
        internalNote = chargeTemplate.getInternalNote();
        
        // New Fields for Parameters
        parameter1Description = chargeTemplate.getParameter1Description();
        parameter1TranslatedDescriptions = LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(chargeTemplate.getParameter1TranslatedDescriptions());
        parameter1TranslatedLongDescriptions = LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(chargeTemplate.getParameter1TranslatedLongDescriptions());
        parameter1Format = chargeTemplate.getParameter1Format();
        parameter1IsMandatory = chargeTemplate.isParameter1IsMandatory();
        parameter1IsHidden = chargeTemplate.isParameter1IsHidden();

        parameter2Description = chargeTemplate.getParameter2Description();
        parameter2TranslatedDescriptions = LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(chargeTemplate.getParameter2TranslatedDescriptions());
        parameter2TranslatedLongDescriptions = LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(chargeTemplate.getParameter2TranslatedLongDescriptions());
        parameter2Format = chargeTemplate.getParameter2Format();
        parameter2IsMandatory = chargeTemplate.isParameter2IsMandatory();
        parameter2IsHidden = chargeTemplate.isParameter2IsHidden();

        parameter3Description = chargeTemplate.getParameter3Description();
        parameter3TranslatedDescriptions = LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(chargeTemplate.getParameter3TranslatedDescriptions());
        parameter3TranslatedLongDescriptions = LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(chargeTemplate.getParameter3TranslatedLongDescriptions());
        parameter3Format = chargeTemplate.getParameter3Format();
        parameter3IsMandatory = chargeTemplate.isParameter3IsMandatory();
        parameter3IsHidden = chargeTemplate.isParameter3IsHidden();

        parameterExtraDescription = chargeTemplate.getParameterExtraDescription();
        parameterExtraTranslatedDescriptions = LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(chargeTemplate.getParameterExtraTranslatedDescriptions());
        parameterExtraTranslatedLongDescriptions = LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(chargeTemplate.getParameterExtraTranslatedLongDescriptions());
        parameterExtraFormat = chargeTemplate.getParameterExtraFormat();
        parameterExtraIsMandatory = chargeTemplate.isParameterExtraIsMandatory();
        parameterExtraIsHidden = chargeTemplate.isParameterExtraIsHidden();

        // Business Key
        businessKeyEl = chargeTemplate.getBusinessKeyEl();
        businessKeyDescription = chargeTemplate.getBusinessKeyDescription();
        businessKeyTranslatedDescriptions = LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(chargeTemplate.getBusinessKeyTranslatedDescriptions());
        businessKeyTranslatedLongDescriptions = LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(chargeTemplate.getBusinessKeyTranslatedLongDescriptions());
        businessKeyFormat = chargeTemplate.getBusinessKeyFormat();
        businessKeyIsMandatory = chargeTemplate.isBusinessKeyIsMandatory();
        businessKeyIsHidden = chargeTemplate.isBusinessKeyIsHidden();
        
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
                + unitNbDecimal + ", customFields=" + customFields + ", triggeredEdrs=" + triggeredEdrs + ",roundingModeDtoEnum=" + roundingModeDtoEnum + ", filterExpression=" + filterExpression;
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

    public List<String> getLinkedAttributes() {
        return linkedAttributes;
    }

    public void setLinkedAttributes(List<String> linkedAttributes) {
        this.linkedAttributes = linkedAttributes;
    }

	/**
	 * @return the status
	 */
	public ChargeTemplateStatusEnum getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(ChargeTemplateStatusEnum status) {
		this.status = status;
	}

	/**
	 * @return the internalNote
	 */
	public String getInternalNote() {
		return internalNote;
	}

	/**
	 * @param internalNote the internalNote to set
	 */
	public void setInternalNote(String internalNote) {
		this.internalNote = internalNote;
	}

    public Set<String> getPricePlanCodes() {
        return pricePlanCodes;
    }

    public void setPricePlanCodes(Set<String> pricePlanCodes) {
        this.pricePlanCodes = pricePlanCodes;
    }

	public String getParameter1Description() {
		return parameter1Description;
	}

	public void setParameter1Description(String parameter1Description) {
		this.parameter1Description = parameter1Description;
	}

	public List<LanguageDescriptionDto> getParameter1TranslatedDescriptions() {
		return parameter1TranslatedDescriptions;
	}

	public void setParameter1TranslatedDescriptions(List<LanguageDescriptionDto> parameter1TranslatedDescriptions) {
		this.parameter1TranslatedDescriptions = parameter1TranslatedDescriptions;
	}

	public List<LanguageDescriptionDto> getParameter1TranslatedLongDescriptions() {
		return parameter1TranslatedLongDescriptions;
	}

	public void setParameter1TranslatedLongDescriptions(List<LanguageDescriptionDto> parameter1TranslatedLongDescriptions) {
		this.parameter1TranslatedLongDescriptions = parameter1TranslatedLongDescriptions;
	}

	public ChargeTemplate.ParameterFormat getParameter1Format() {
		return parameter1Format;
	}

	public void setParameter1Format(ChargeTemplate.ParameterFormat parameter1Format) {
		this.parameter1Format = parameter1Format;
	}

	public Boolean getParameter1IsMandatory() {
		return parameter1IsMandatory;
	}

	public void setParameter1IsMandatory(Boolean parameter1IsMandatory) {
		this.parameter1IsMandatory = parameter1IsMandatory;
	}

	public Boolean getParameter1IsHidden() {
		return parameter1IsHidden;
	}

	public void setParameter1IsHidden(Boolean parameter1IsHidden) {
		this.parameter1IsHidden = parameter1IsHidden;
	}

	public String getParameter2Description() {
		return parameter2Description;
	}

	public void setParameter2Description(String parameter2Description) {
		this.parameter2Description = parameter2Description;
	}

	public List<LanguageDescriptionDto> getParameter2TranslatedDescriptions() {
		return parameter2TranslatedDescriptions;
	}

	public void setParameter2TranslatedDescriptions(List<LanguageDescriptionDto> parameter2TranslatedDescriptions) {
		this.parameter2TranslatedDescriptions = parameter2TranslatedDescriptions;
	}

	public List<LanguageDescriptionDto> getParameter2TranslatedLongDescriptions() {
		return parameter2TranslatedLongDescriptions;
	}

	public void setParameter2TranslatedLongDescriptions(List<LanguageDescriptionDto> parameter2TranslatedLongDescriptions) {
		this.parameter2TranslatedLongDescriptions = parameter2TranslatedLongDescriptions;
	}

	public ChargeTemplate.ParameterFormat getParameter2Format() {
		return parameter2Format;
	}

	public void setParameter2Format(ChargeTemplate.ParameterFormat parameter2Format) {
		this.parameter2Format = parameter2Format;
	}

	public Boolean getParameter2IsMandatory() {
		return parameter2IsMandatory;
	}

	public void setParameter2IsMandatory(Boolean parameter2IsMandatory) {
		this.parameter2IsMandatory = parameter2IsMandatory;
	}

	public Boolean getParameter2IsHidden() {
		return parameter2IsHidden;
	}

	public void setParameter2IsHidden(Boolean parameter2IsHidden) {
		this.parameter2IsHidden = parameter2IsHidden;
	}

	public String getParameter3Description() {
		return parameter3Description;
	}

	public void setParameter3Description(String parameter3Description) {
		this.parameter3Description = parameter3Description;
	}

	public List<LanguageDescriptionDto> getParameter3TranslatedDescriptions() {
		return parameter3TranslatedDescriptions;
	}

	public void setParameter3TranslatedDescriptions(List<LanguageDescriptionDto> parameter3TranslatedDescriptions) {
		this.parameter3TranslatedDescriptions = parameter3TranslatedDescriptions;
	}

	public List<LanguageDescriptionDto> getParameter3TranslatedLongDescriptions() {
		return parameter3TranslatedLongDescriptions;
	}

	public void setParameter3TranslatedLongDescriptions(List<LanguageDescriptionDto> parameter3TranslatedLongDescriptions) {
		this.parameter3TranslatedLongDescriptions = parameter3TranslatedLongDescriptions;
	}

	public ChargeTemplate.ParameterFormat getParameter3Format() {
		return parameter3Format;
	}

	public void setParameter3Format(ChargeTemplate.ParameterFormat parameter3Format) {
		this.parameter3Format = parameter3Format;
	}

	public Boolean getParameter3IsMandatory() {
		return parameter3IsMandatory;
	}

	public void setParameter3IsMandatory(Boolean parameter3IsMandatory) {
		this.parameter3IsMandatory = parameter3IsMandatory;
	}

	public Boolean getParameter3IsHidden() {
		return parameter3IsHidden;
	}

	public void setParameter3IsHidden(Boolean parameter3IsHidden) {
		this.parameter3IsHidden = parameter3IsHidden;
	}

	public String getParameterExtraDescription() {
		return parameterExtraDescription;
	}

	public void setParameterExtraDescription(String parameterExtraDescription) {
		this.parameterExtraDescription = parameterExtraDescription;
	}

	public List<LanguageDescriptionDto> getParameterExtraTranslatedDescriptions() {
		return parameterExtraTranslatedDescriptions;
	}

	public void setParameterExtraTranslatedDescriptions(List<LanguageDescriptionDto> parameterExtraTranslatedDescriptions) {
		this.parameterExtraTranslatedDescriptions = parameterExtraTranslatedDescriptions;
	}

	public List<LanguageDescriptionDto> getParameterExtraTranslatedLongDescriptions() {
		return parameterExtraTranslatedLongDescriptions;
	}

	public void setParameterExtraTranslatedLongDescriptions(
			List<LanguageDescriptionDto> parameterExtraTranslatedLongDescriptions) {
		this.parameterExtraTranslatedLongDescriptions = parameterExtraTranslatedLongDescriptions;
	}

	public ChargeTemplate.ParameterFormat getParameterExtraFormat() {
		return parameterExtraFormat;
	}

	public void setParameterExtraFormat(ChargeTemplate.ParameterFormat parameterExtraFormat) {
		this.parameterExtraFormat = parameterExtraFormat;
	}

	public Boolean getParameterExtraIsMandatory() {
		return parameterExtraIsMandatory;
	}

	public void setParameterExtraIsMandatory(Boolean parameterExtraIsMandatory) {
		this.parameterExtraIsMandatory = parameterExtraIsMandatory;
	}

	public Boolean getParameterExtraIsHidden() {
		return parameterExtraIsHidden;
	}

	public void setParameterExtraIsHidden(Boolean parameterExtraIsHidden) {
		this.parameterExtraIsHidden = parameterExtraIsHidden;
	}

	public String getBusinessKeyEl() {
		return businessKeyEl;
	}

	public void setBusinessKeyEl(String businessKeyEl) {
		this.businessKeyEl = businessKeyEl;
	}

	public String getBusinessKeyDescription() {
		return businessKeyDescription;
	}

	public void setBusinessKeyDescription(String businessKeyDescription) {
		this.businessKeyDescription = businessKeyDescription;
	}

	public List<LanguageDescriptionDto> getBusinessKeyTranslatedDescriptions() {
		return businessKeyTranslatedDescriptions;
	}

	public void setBusinessKeyTranslatedDescriptions(List<LanguageDescriptionDto> businessKeyTranslatedDescriptions) {
		this.businessKeyTranslatedDescriptions = businessKeyTranslatedDescriptions;
	}

	public List<LanguageDescriptionDto> getBusinessKeyTranslatedLongDescriptions() {
		return businessKeyTranslatedLongDescriptions;
	}

	public void setBusinessKeyTranslatedLongDescriptions(List<LanguageDescriptionDto> businessKeyTranslatedLongDescriptions) {
		this.businessKeyTranslatedLongDescriptions = businessKeyTranslatedLongDescriptions;
	}

	public ChargeTemplate.BusinessKeyFormat getBusinessKeyFormat() {
		return businessKeyFormat;
	}

	public void setBusinessKeyFormat(ChargeTemplate.BusinessKeyFormat businessKeyFormat) {
		this.businessKeyFormat = businessKeyFormat;
	}

	public Boolean getBusinessKeyIsMandatory() {
		return businessKeyIsMandatory;
	}

	public void setBusinessKeyIsMandatory(Boolean businessKeyIsMandatory) {
		this.businessKeyIsMandatory = businessKeyIsMandatory;
	}

	public Boolean getBusinessKeyIsHidden() {
		return businessKeyIsHidden;
	}

	public void setBusinessKeyIsHidden(Boolean businessKeyIsHidden) {
		this.businessKeyIsHidden = businessKeyIsHidden;
	}
    
}