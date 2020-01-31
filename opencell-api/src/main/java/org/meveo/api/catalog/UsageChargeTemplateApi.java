package org.meveo.api.catalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.catalog.TriggeredEdrTemplateDto;
import org.meveo.api.dto.catalog.UsageChargeTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.catalog.UnitOfMeasure;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.finance.RevenueRecognitionRule;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.TriggeredEDRTemplateService;
import org.meveo.service.catalog.impl.UnitOfMeasureService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;
import org.meveo.service.finance.RevenueRecognitionRuleService;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 **/
@Stateless
public class UsageChargeTemplateApi extends BaseCrudApi<UsageChargeTemplate, UsageChargeTemplateDto> {

    @Inject
    private UsageChargeTemplateService usageChargeTemplateService;

    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    private TriggeredEDRTemplateService triggeredEDRTemplateService;

    @Inject
    private RevenueRecognitionRuleService revenueRecognitionRuleService;
    
    @Inject
    private UnitOfMeasureService unitOfMeasureService;

    @Override
    public UsageChargeTemplate create(UsageChargeTemplateDto postData) throws MeveoApiException, BusinessException {
    	
    	UsageChargeTemplate chargeTemplate = checkByCode(postData);
        if (chargeTemplate != null) {
            throw new EntityAlreadyExistsException(UsageChargeTemplate.class, postData.getCode());
        }
    	chargeTemplate = dtoToBo(postData, chargeTemplate);
		if (postData.isDisabled() != null) {
			chargeTemplate.setDisabled(postData.isDisabled());
		}
        usageChargeTemplateService.create(chargeTemplate);
        return chargeTemplate;
    }

    @Override
    public UsageChargeTemplate update(UsageChargeTemplateDto postData) throws MeveoApiException, BusinessException {
    	UsageChargeTemplate chargeTemplate = checkByCode(postData);
        if (chargeTemplate == null) {
            throw new EntityDoesNotExistsException(UsageChargeTemplate.class, postData.getCode());
        }
        chargeTemplate = dtoToBo(postData, chargeTemplate);
        return usageChargeTemplateService.update(chargeTemplate);
    }

	private UsageChargeTemplate dtoToBo(UsageChargeTemplateDto postData, UsageChargeTemplate chargeTemplate)
			throws MeveoApiException, BusinessException {
		boolean isNew=chargeTemplate==null;
		if(isNew) {
	        chargeTemplate = new UsageChargeTemplate();
	        chargeTemplate.setCode(postData.getCode());
		}else {
	        chargeTemplate.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
		}

        InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(postData.getInvoiceSubCategory());
        if (invoiceSubCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceSubCategory.class, postData.getInvoiceSubCategory());
        }

        chargeTemplate.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        chargeTemplate.setDescription(postData.getDescription());
        chargeTemplate.setAmountEditable(postData.getAmountEditable());
        chargeTemplate.setPriority(postData.getPriority());
        chargeTemplate.setFilterParam1(postData.getFilterParam1());
        chargeTemplate.setFilterParam2(postData.getFilterParam2());
        chargeTemplate.setFilterParam3(postData.getFilterParam3());
        chargeTemplate.setFilterParam4(postData.getFilterParam4());
        if (postData.getFilterExpression() != null) {
            chargeTemplate.setFilterExpression(postData.getFilterExpression());
        }
        if (postData.getFilterExpressionSpark() != null) {
            chargeTemplate.setFilterExpressionSpark(postData.getFilterExpressionSpark());
        }
        chargeTemplate.setInvoiceSubCategory(invoiceSubCategory);
        chargeTemplate.setUnitMultiplicator(postData.getUnitMultiplicator());
        chargeTemplate.setRatingUnitDescription(postData.getRatingUnitDescription());
        chargeTemplate.setUnitNbDecimal(postData.getUnitNbDecimal());
        chargeTemplate.setInputUnitDescription(postData.getInputUnitDescription());
        chargeTemplate.setTriggerNextCharge(postData.getTriggerNextCharge());
        chargeTemplate.setTriggerNextChargeEL(postData.getTriggerNextChargeEL());
        if (postData.getRoundingModeDtoEnum() != null) {
            chargeTemplate.setRoundingMode(postData.getRoundingModeDtoEnum());
        } else {
            chargeTemplate.setRoundingMode(RoundingModeEnum.NEAREST);
        }

        if (postData.getRevenueRecognitionRuleCode() != null) {
            RevenueRecognitionRule revenueRecognitionRule = revenueRecognitionRuleService.findByCode(postData.getRevenueRecognitionRuleCode());
            chargeTemplate.setRevenueRecognitionRule(revenueRecognitionRule);
        }

        if (postData.getLanguageDescriptions() != null) {
            chargeTemplate.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), chargeTemplate.getDescriptionI18n()));
        }
        
        chargeTemplate.setInputUnitOfMeasure(checkUnitOfMeasure(postData.getInputUnitOfMeasureCode()));
        chargeTemplate.setRatingUnitOfMeasure(checkUnitOfMeasure(postData.getRatingUnitOfMeasureCode()));

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
		return chargeTemplate;
	}

	private UnitOfMeasure checkUnitOfMeasure( String ratingUnitOfMeasureCode) throws EntityDoesNotExistsException {
		UnitOfMeasure ratingUOM =null;
		if(ratingUnitOfMeasureCode!=null) {
			ratingUOM = unitOfMeasureService.findByCode(ratingUnitOfMeasureCode);
			if(ratingUOM==null) {
				 throw new EntityDoesNotExistsException(UnitOfMeasure.class, ratingUnitOfMeasureCode);
			}
        }
		return ratingUOM;
	}

	private UsageChargeTemplate checkByCode(UsageChargeTemplateDto postData) throws MeveoApiException {
		if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getInvoiceSubCategory())) {
            missingParameters.add("invoiceSubCategory");
        }

        handleMissingParametersAndValidate(postData);

        // check if code already exists
        UsageChargeTemplate chargeTemplate = usageChargeTemplateService.findByCode(postData.getCode());
		return chargeTemplate;
	}

    @Override
    public UsageChargeTemplateDto find(String code) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("usageChargeTemplateCode");
            handleMissingParameters();
        }

        UsageChargeTemplateDto result;

        // check if code already exists
        UsageChargeTemplate chargeTemplate = usageChargeTemplateService.findByCode(code, Arrays.asList("invoiceSubCategory"));
        if (chargeTemplate == null) {
            throw new EntityDoesNotExistsException(UsageChargeTemplateDto.class, code);
        }

        result = new UsageChargeTemplateDto(chargeTemplate, entityToDtoConverter.getCustomFieldsDTO(chargeTemplate, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));

        return result;
    }
}