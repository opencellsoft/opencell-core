package org.meveo.api.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.catalog.ChargeTemplateDto;
import org.meveo.api.dto.catalog.TriggeredEdrTemplateDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.catalog.UnitOfMeasure;
import org.meveo.model.finance.RevenueRecognitionRule;
import org.meveo.model.tax.TaxClass;
import org.meveo.service.catalog.impl.ChargeTemplateServiceAll;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.TriggeredEDRTemplateService;
import org.meveo.service.catalog.impl.UnitOfMeasureService;
import org.meveo.service.finance.RevenueRecognitionRuleService;
import org.meveo.service.tax.TaxClassService;

/**
 * @author Edward P. Legaspi
 **/

public abstract class ChargeTemplateApi<E extends ChargeTemplate, T extends ChargeTemplateDto> extends BaseCrudApi<E, T> {

    @Inject
    private ChargeTemplateServiceAll chargeTemplateService;

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
        if (postData.getFilterExpressionSpark() != null) {
            chargeTemplate.setFilterExpressionSpark(postData.getFilterExpressionSpark());
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
        if (postData.getTaxClassElSpark() != null) {
            chargeTemplate.setTaxClassElSpark(StringUtils.getDefaultIfEmpty(postData.getTaxClassElSpark(), null));
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

        if (isNew && postData.isDisabled() != null) {
            chargeTemplate.setDisabled(postData.isDisabled());
        }

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