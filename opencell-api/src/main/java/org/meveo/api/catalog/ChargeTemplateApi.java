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

package org.meveo.api.catalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.xml.bind.ValidationException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.LanguageDto;
import org.meveo.api.dto.catalog.ChargeTemplateDto;
import org.meveo.api.dto.catalog.TriggeredEdrTemplateDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.ListUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.ChargeTemplateStatusEnum;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.catalog.UnitOfMeasure;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.enums.AttributeTypeEnum;
import org.meveo.model.finance.RevenueRecognitionRule;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.tax.TaxClass;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.TriggeredEDRTemplateService;
import org.meveo.service.catalog.impl.UnitOfMeasureService;
import org.meveo.service.cpq.AttributeService;
import org.meveo.service.finance.RevenueRecognitionRuleService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.tax.TaxClassService;

/**
 * @author Edward P. Legaspi
 **/

public abstract class ChargeTemplateApi<E extends ChargeTemplate, T extends ChargeTemplateDto> extends BaseCrudApi<E, T> {

    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    private TriggeredEDRTemplateService triggeredEDRTemplateService;

    @Inject
    private RevenueRecognitionRuleService revenueRecognitionRuleService;

    @Inject
    private TaxClassService taxClassService;

    @Inject
    private UnitOfMeasureService unitOfMeasureService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private AttributeService attributeService;

    @Inject
    private PricePlanMatrixService pricePlanMatrixService;
    
    @Inject
    private TradingLanguageService tradingLanguageService;

    /**
     * Convert/update DTO object to an entity object
     * 
     * @param postData DTO object
     * @param chargeTemplate Entity object to update
     * @throws MeveoApiException General API exception
     * @throws BusinessException General exception
     */
    protected void dtoToEntity(T postData, E chargeTemplate, boolean isNew) throws MeveoApiException, BusinessException {

        if (postData.getInvoiceSubCategory() != null) {
            InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(postData.getInvoiceSubCategory());
            if (invoiceSubCategory == null) {
                throw new EntityDoesNotExistsException(InvoiceSubCategory.class, postData.getInvoiceSubCategory());
            }
            chargeTemplate.setInvoiceSubCategory(invoiceSubCategory);
        }

        if (postData.getDescription() != null) {
            chargeTemplate.setDescription(StringUtils.getDefaultIfEmpty(postData.getDescription(), null));
        }

        if (postData.getAmountEditable() != null) {
            chargeTemplate.setAmountEditable(postData.getAmountEditable());
        }

        if (postData.getUnitMultiplicator() != null) {
            chargeTemplate.setUnitMultiplicator(postData.getUnitMultiplicator());
        }
        if (postData.getRatingUnitDescription() != null) {
            chargeTemplate.setRatingUnitDescription(StringUtils.getDefaultIfEmpty(postData.getRatingUnitDescription(), null));
        }
        if (postData.getUnitNbDecimal() != null) {
            chargeTemplate.setUnitNbDecimal(postData.getUnitNbDecimal());
        }
        if (postData.getInputUnitDescription() != null) {
            chargeTemplate.setInputUnitDescription(StringUtils.getDefaultIfEmpty(postData.getInputUnitDescription(), null));
        }
        if (postData.getInputUnitOfMeasureCode() != null) {
            chargeTemplate.setInputUnitOfMeasure(StringUtils.isBlank(postData.getInputUnitOfMeasureCode()) ? null : checkUnitOfMeasure(postData.getInputUnitOfMeasureCode()));
        }
        if (postData.getRatingUnitOfMeasureCode() != null) {
            chargeTemplate.setRatingUnitOfMeasure(StringUtils.isBlank(postData.getRatingUnitOfMeasureCode()) ? null : checkUnitOfMeasure(postData.getRatingUnitOfMeasureCode()));
        }

        if (postData.getFilterExpression() != null) {
            chargeTemplate.setFilterExpression(StringUtils.getDefaultIfEmpty(postData.getFilterExpression(), null));
        }

        if (postData.getRoundingModeDtoEnum() != null) {
            chargeTemplate.setRoundingMode(postData.getRoundingModeDtoEnum());
        } else if (isNew) {
            chargeTemplate.setRoundingMode(RoundingModeEnum.NEAREST);
        }

        if (postData.getRevenueRecognitionRuleCode() != null) {
            if (StringUtils.isBlank(postData.getRevenueRecognitionRuleCode())) {
                chargeTemplate.setRevenueRecognitionRule(null);
            } else {
                RevenueRecognitionRule revenueRecognitionRule = revenueRecognitionRuleService.findByCode(postData.getRevenueRecognitionRuleCode());
                chargeTemplate.setRevenueRecognitionRule(revenueRecognitionRule);
            }
        }

        if (postData.getLanguageDescriptions() != null) {
            chargeTemplate.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), null));
        }
        if (postData.getTaxClassCode() != null) {
            TaxClass taxClass = taxClassService.findByCode(postData.getTaxClassCode());
            if (taxClass == null) {
                throw new EntityDoesNotExistsException(TaxClass.class, postData.getTaxClassCode());
            } else {
                chargeTemplate.setTaxClass(taxClass);
            }
        }
        if (postData.getTaxClassEl() != null) {
            chargeTemplate.setTaxClassEl(StringUtils.getDefaultIfEmpty(postData.getTaxClassEl(), null));
        }

        if (postData.getTriggeredEdrs() != null) {
            List<TriggeredEDRTemplate> edrTemplates = new ArrayList<TriggeredEDRTemplate>();

            for (TriggeredEdrTemplateDto triggeredEdrTemplateDto : postData.getTriggeredEdrs().getTriggeredEdr()) {
                TriggeredEDRTemplate triggeredEdrTemplate = triggeredEDRTemplateService.findByCode(triggeredEdrTemplateDto.getCode());
                if (triggeredEdrTemplate == null) {
                    throw new EntityDoesNotExistsException(TriggeredEDRTemplate.class, triggeredEdrTemplateDto.getCode());
                }
                edrTemplates.add(triggeredEdrTemplate);
            }

            chargeTemplate.setEdrTemplates(edrTemplates);
        }

        if (postData.getRatingScriptCode() != null) {
            if (StringUtils.isBlank(postData.getRatingScriptCode())) {
                chargeTemplate.setRatingScript(null);
            } else {
                ScriptInstance ratingScript = scriptInstanceService.findByCode(postData.getRatingScriptCode());
                chargeTemplate.setRatingScript(ratingScript);
            }
        }

        if (!ListUtils.isEmtyCollection(postData.getPricePlanCodes())) {
            Set<PricePlanMatrix> pricePlans = postData.getPricePlanCodes()
                                                        .stream()
                                                        .map(c -> Optional.ofNullable(pricePlanMatrixService.findByCode(c))
                                                                            .orElseThrow(() -> new EntityDoesNotExistsException(PricePlanMatrix.class, c)))
                                                        .collect(Collectors.toSet());

            pricePlans.forEach(pp -> {
                if(pp.getChargeTemplates() == null) {
                    pp.setChargeTemplates(new HashSet<>());
                }
                pp.getChargeTemplates().add(chargeTemplate);
            });
        }

        if (isNew && postData.isDisabled() != null) {
            chargeTemplate.setDisabled(postData.isDisabled());
        }
        chargeTemplate.setDropZeroWo(postData.isDropZeroWo());
        if (postData.getSortIndexEl() != null) {
            chargeTemplate.setSortIndexEl(postData.getSortIndexEl());
        }
        if(postData.getLinkedAttributes() != null){
            chargeTemplate.getAttributes()
                    .forEach(
                            attribute -> attribute.getChargeTemplates().remove(chargeTemplate)
                    );
            Set<Attribute> linkedAttributes = postData.getLinkedAttributes()
                    .stream()
                    .map(linkedAttribute -> {
                        Attribute attribute = loadEntityByCode(attributeService, linkedAttribute, Attribute.class);
                        if (attribute.getAttributeType() != AttributeTypeEnum.BOOLEAN)
                            throw new BusinessApiException("Attribute: " + attribute.getCode() + " not of type Boolean");
                        attribute.getChargeTemplates().add(chargeTemplate);
                        return attribute;
                    })
                    .collect(Collectors.toSet());
            chargeTemplate.getAttributes().addAll(linkedAttributes);
        }
        if(postData.getStatus() != null){
            try {
                chargeTemplate.setStatus(postData.getStatus());
            } catch (ValidationException e) {
                throw new RuntimeException(e);
            }
        }

        if(postData.getInternalNote() != null) {
        	chargeTemplate.setInternalNote(StringUtils.isBlank(postData.getInternalNote()) ? null : postData.getInternalNote());
        }
        
        chargeTemplate.setParameter1Description(StringUtils.isBlank(postData.getParameter1Description()) ? null : postData.getParameter1Description());
        chargeTemplate.setParameter1TranslatedDescriptions(mapLanguageDtoToTradingLanguage(postData.getParameter1TranslatedDescriptions(), isNew, "1"));
        chargeTemplate.setParameter1TranslatedLongDescriptions(mapLanguageDtoToTradingLanguage(postData.getParameter1TranslatedLongDescriptions()));
        chargeTemplate.setParameter1Format(StringUtils.isBlank(postData.getParameter1Format()) ? null : postData.getParameter1Format());
        chargeTemplate.setParameter1IsMandatory(postData.getParameter1IsMandatory() != null ? postData.getParameter1IsMandatory() : false);
        chargeTemplate.setParameter1IsHidden(postData.getParameter1IsHidden() != null ? postData.getParameter1IsHidden() : false);

        chargeTemplate.setParameter2Description(StringUtils.isBlank(postData.getParameter2Description()) ? null : postData.getParameter2Description());
        chargeTemplate.setParameter2TranslatedDescriptions(mapLanguageDtoToTradingLanguage(postData.getParameter2TranslatedDescriptions(), isNew, "2"));
        chargeTemplate.setParameter2TranslatedLongDescriptions(mapLanguageDtoToTradingLanguage(postData.getParameter2TranslatedLongDescriptions()));
        chargeTemplate.setParameter2Format(StringUtils.isBlank(postData.getParameter2Format()) ? null : postData.getParameter2Format());
        chargeTemplate.setParameter2IsMandatory(postData.getParameter2IsMandatory() != null ? postData.getParameter2IsMandatory() : false);
        chargeTemplate.setParameter2IsHidden(postData.getParameter2IsHidden() != null ? postData.getParameter2IsHidden() : false);

        chargeTemplate.setParameter3Description(StringUtils.isBlank(postData.getParameter3Description()) ? null : postData.getParameter3Description());
        chargeTemplate.setParameter3TranslatedDescriptions(mapLanguageDtoToTradingLanguage(postData.getParameter3TranslatedDescriptions(), isNew, "3"));
        chargeTemplate.setParameter3TranslatedLongDescriptions(mapLanguageDtoToTradingLanguage(postData.getParameter3TranslatedLongDescriptions()));
        chargeTemplate.setParameter3Format(StringUtils.isBlank(postData.getParameter3Format()) ? null : postData.getParameter3Format());
        chargeTemplate.setParameter3IsMandatory(postData.getParameter3IsMandatory() != null ? postData.getParameter3IsMandatory() : false);
        chargeTemplate.setParameter3IsHidden(postData.getParameter3IsHidden() != null ? postData.getParameter3IsHidden() : false);

        chargeTemplate.setParameterExtraDescription(StringUtils.isBlank(postData.getParameterExtraDescription()) ? null : postData.getParameterExtraDescription());
        chargeTemplate.setParameterExtraTranslatedDescriptions(mapLanguageDtoToTradingLanguage(postData.getParameterExtraTranslatedDescriptions(), isNew, "Extra"));
        chargeTemplate.setParameterExtraTranslatedLongDescriptions(mapLanguageDtoToTradingLanguage(postData.getParameterExtraTranslatedLongDescriptions()));
        chargeTemplate.setParameterExtraFormat(StringUtils.isBlank(postData.getParameterExtraFormat()) ? null : postData.getParameterExtraFormat());
        chargeTemplate.setExtraIsMandatory(postData.getParameterExtraIsMandatory() != null ? postData.getParameterExtraIsMandatory() : false);
        chargeTemplate.setParameterExtraIsHidden(postData.getParameterExtraIsHidden() != null ? postData.getParameterExtraIsHidden() : false);



        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), chargeTemplate, isNew);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
    }
    
    private Map<String, String> mapLanguageDtoToTradingLanguage(Map<String, String> languageDtoMap, boolean isNew, String parameterNumber) {
        Map<String, String> tradingLanguageMap = new HashMap<>();

        if (languageDtoMap != null  && !languageDtoMap.isEmpty()) {
            for (Map.Entry<String, String> entry : languageDtoMap.entrySet()) {
                String languageCode = entry.getKey();
                TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(languageCode);
                if(tradingLanguage == null) {
                	throw new EntityDoesNotExistsException(" TradingLanguage with code=" + languageCode
            				+ " does not exists.");
                }
                tradingLanguageMap.put(tradingLanguage.getLanguageCode(), entry.getValue());
            }
        } else if (isNew) {
            tradingLanguageMap.put("ENG", "Parameter " + parameterNumber);
            tradingLanguageMap.put("FRA", "Paramètre " + parameterNumber);
        }

        return tradingLanguageMap;
    }
    
    private Map<String, String> mapLanguageDtoToTradingLanguage(Map<String, String> languageDtoMap) {
        Map<String, String> tradingLanguageMap = new HashMap<>();
        for (Map.Entry<String, String> entry : languageDtoMap.entrySet()) {
            String languageCode = entry.getKey();
            TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(languageCode);
            if(tradingLanguage == null) {
            	throw new EntityDoesNotExistsException(" TradingLanguage with code=" + languageCode
        				+ " does not exists.");
            }
            tradingLanguageMap.put(tradingLanguage.getLanguageCode(), entry.getValue());
        }
        return tradingLanguageMap;
    }

    private TradingLanguage findTradingLanguageByLanguageDto(LanguageDto languageDto) {
    	return tradingLanguageService.findByTradingLanguageCode(languageDto.getCode());
    }


    protected void checkInternalNote(ChargeTemplate entity, ChargeTemplateDto postData) {
    	// Internal note updatable only for Draft and Active Charge template
		if (postData.getInternalNote() != null && !postData.getInternalNote().equals(entity.getInternalNote()) && 
				!Arrays.asList(ChargeTemplateStatusEnum.DRAFT, ChargeTemplateStatusEnum.ACTIVE).contains(entity.getStatus())) {
			throw new InvalidParameterException("Cannot modify internalNote if charge is not DRAFT or ACTIVATED");
		}
    }

    private UnitOfMeasure checkUnitOfMeasure(String ratingUnitOfMeasureCode) throws EntityDoesNotExistsException {
        UnitOfMeasure ratingUOM = null;
        if (ratingUnitOfMeasureCode != null) {
            ratingUOM = unitOfMeasureService.findByCode(ratingUnitOfMeasureCode);
            if (ratingUOM == null) {
                throw new EntityDoesNotExistsException(UnitOfMeasure.class, ratingUnitOfMeasureCode);
            }
        }
        return ratingUOM;
    }
}