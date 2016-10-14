package org.meveo.api.catalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.api.dto.catalog.TriggeredEdrTemplateDto;
import org.meveo.api.dto.catalog.UsageChargeTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.finance.RevenueRecognitionRule;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.TriggeredEDRTemplateService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;
import org.meveo.service.finance.RevenueRecognitionRuleService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class UsageChargeTemplateApi extends BaseCrudApi<UsageChargeTemplate, UsageChargeTemplateDto> {

    @Inject
    private UsageChargeTemplateService usageChargeTemplateService;

    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    private CatMessagesService catMessagesService;

    @Inject
    private TriggeredEDRTemplateService triggeredEDRTemplateService;

    @Inject
    private RevenueRecognitionRuleService revenueRecognitionRuleService;
    

    public UsageChargeTemplate create(UsageChargeTemplateDto postData, User currentUser) throws MeveoApiException, BusinessException {

        validate(postData);

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getInvoiceSubCategory())) {
            missingParameters.add("invoiceSubCategory");
        }

        handleMissingParameters();
        

        Provider provider = currentUser.getProvider();

        // check if code already exists
        if (usageChargeTemplateService.findByCode(postData.getCode(), provider) != null) {
            throw new EntityAlreadyExistsException(UsageChargeTemplate.class, postData.getCode());
        }

        InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(postData.getInvoiceSubCategory(), provider);
        if (invoiceSubCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceSubCategory.class, postData.getInvoiceSubCategory());
        }

        if (provider.getTradingLanguages() != null) {
            if (postData.getLanguageDescriptions() != null) {
                for (LanguageDescriptionDto ld : postData.getLanguageDescriptions()) {
                    boolean match = false;

                    for (TradingLanguage tl : provider.getTradingLanguages()) {
                        if (tl.getLanguageCode().equals(ld.getLanguageCode())) {
                            match = true;
                            break;
                        }
                    }

                    if (!match) {
                        throw new MeveoApiException(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, "Language " + ld.getLanguageCode() + " is not supported by the provider.");
                    }
                }
            }
        }

        UsageChargeTemplate chargeTemplate = new UsageChargeTemplate();
        chargeTemplate.setCode(postData.getCode());
        chargeTemplate.setDescription(postData.getDescription());
        chargeTemplate.setDisabled(postData.isDisabled());
        chargeTemplate.setAmountEditable(postData.getAmountEditable());
        chargeTemplate.setUnitMultiplicator(postData.getUnitMultiplicator());
        chargeTemplate.setRatingUnitDescription(postData.getRatingUnitDescription());
        chargeTemplate.setUnitNbDecimal(postData.getUnitNbDecimal());
        chargeTemplate.setInputUnitDescription(postData.getInputUnitDescription());
        chargeTemplate.setPriority(postData.getPriority());
        chargeTemplate.setFilterParam1(postData.getFilterParam1());
        chargeTemplate.setFilterParam2(postData.getFilterParam2());
        chargeTemplate.setFilterParam3(postData.getFilterParam3());
        chargeTemplate.setFilterParam4(postData.getFilterParam4());
        chargeTemplate.setFilterExpression(postData.getFilterExpression());
        chargeTemplate.setInvoiceSubCategory(invoiceSubCategory);
        chargeTemplate.setUnitMultiplicator(postData.getUnitMultiplicator());
        chargeTemplate.setRatingUnitDescription(postData.getRatingUnitDescription());
        chargeTemplate.setUnitNbDecimal(postData.getUnitNbDecimal());
        chargeTemplate.setInputUnitDescription(postData.getInputUnitDescription());
        if (postData.getRoundingModeDtoEnum() != null) {
            chargeTemplate.setRoundingMode(postData.getRoundingModeDtoEnum());
        } else {
            chargeTemplate.setRoundingMode(RoundingModeEnum.NEAREST);
        }

        
        if(postData.getRevenueRecognitionRuleCode()!=null){
        	RevenueRecognitionRule revenueRecognitionRule = revenueRecognitionRuleService.findByCode(postData.getRevenueRecognitionRuleCode(), provider);
        	chargeTemplate.setRevenueRecognitionRule(revenueRecognitionRule);
        }
        
        if (postData.getTriggeredEdrs() != null) {
            List<TriggeredEDRTemplate> edrTemplates = new ArrayList<TriggeredEDRTemplate>();

            for (TriggeredEdrTemplateDto triggeredEdrTemplateDto : postData.getTriggeredEdrs().getTriggeredEdr()) {
                TriggeredEDRTemplate triggeredEdrTemplate = triggeredEDRTemplateService.findByCode(triggeredEdrTemplateDto.getCode(), provider);
                if (triggeredEdrTemplate == null) {
                    throw new EntityDoesNotExistsException(TriggeredEDRTemplate.class, triggeredEdrTemplateDto.getCode());
                }

                edrTemplates.add(triggeredEdrTemplate);
            }

            chargeTemplate.setEdrTemplates(edrTemplates);
        }

        usageChargeTemplateService.create(chargeTemplate, currentUser);

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), chargeTemplate, true, currentUser);
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        // create cat messages
        if (postData.getLanguageDescriptions() != null) {
            for (LanguageDescriptionDto ld : postData.getLanguageDescriptions()) {
                CatMessages catMessages = new CatMessages(UsageChargeTemplate.class.getSimpleName() , chargeTemplate.getCode(), ld.getLanguageCode(), ld.getDescription());

                catMessagesService.create(catMessages, currentUser);
            }
        }
        
        return chargeTemplate;
    }

    public UsageChargeTemplate update(UsageChargeTemplateDto postData, User currentUser) throws MeveoApiException, BusinessException {

        validate(postData);

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getInvoiceSubCategory())) {
            missingParameters.add("invoiceSubCategory");
        }

        handleMissingParameters();
        

        Provider provider = currentUser.getProvider();

        // check if code already exists
        UsageChargeTemplate chargeTemplate = usageChargeTemplateService.findByCode(postData.getCode(), provider);
        if (chargeTemplate == null) {
            throw new EntityDoesNotExistsException(UsageChargeTemplate.class, postData.getCode());
        }

        InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(postData.getInvoiceSubCategory(), provider);
        if (invoiceSubCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceSubCategory.class, postData.getInvoiceSubCategory());
        }

        if (provider.getTradingLanguages() != null) {
            if (postData.getLanguageDescriptions() != null) {
                for (LanguageDescriptionDto ld : postData.getLanguageDescriptions()) {
                    boolean match = false;

                    for (TradingLanguage tl : provider.getTradingLanguages()) {
                        if (tl.getLanguageCode().equals(ld.getLanguageCode())) {
                            match = true;
                            break;
                        }
                    }

                    if (!match) {
                        throw new MeveoApiException(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, "Language " + ld.getLanguageCode() + " is not supported by the provider.");
                    }
                }
            }
        }

        chargeTemplate.setDescription(postData.getDescription());
        chargeTemplate.setDisabled(postData.isDisabled());
        chargeTemplate.setAmountEditable(postData.getAmountEditable());
        chargeTemplate.setUnitMultiplicator(postData.getUnitMultiplicator());
        chargeTemplate.setRatingUnitDescription(postData.getRatingUnitDescription());
        chargeTemplate.setUnitNbDecimal(postData.getUnitNbDecimal());
        chargeTemplate.setInputUnitDescription(postData.getInputUnitDescription());
        chargeTemplate.setPriority(postData.getPriority());
        chargeTemplate.setFilterParam1(postData.getFilterParam1());
        chargeTemplate.setFilterParam2(postData.getFilterParam2());
        chargeTemplate.setFilterParam3(postData.getFilterParam3());
        chargeTemplate.setFilterParam4(postData.getFilterParam4());
        chargeTemplate.setFilterExpression(postData.getFilterExpression());
        chargeTemplate.setInvoiceSubCategory(invoiceSubCategory);
        chargeTemplate.setUnitMultiplicator(postData.getUnitMultiplicator());
        chargeTemplate.setRatingUnitDescription(postData.getRatingUnitDescription());
        chargeTemplate.setUnitNbDecimal(postData.getUnitNbDecimal());
        chargeTemplate.setInputUnitDescription(postData.getInputUnitDescription());
        if (postData.getRoundingModeDtoEnum() != null) {
            chargeTemplate.setRoundingMode(postData.getRoundingModeDtoEnum());
        } else {
            chargeTemplate.setRoundingMode(RoundingModeEnum.NEAREST);
        }
        
        if(postData.getRevenueRecognitionRuleCode()!=null){
        	RevenueRecognitionRule revenueRecognitionRule = revenueRecognitionRuleService.findByCode(postData.getRevenueRecognitionRuleCode(), provider);
        	chargeTemplate.setRevenueRecognitionRule(revenueRecognitionRule);
        }

        if (provider.getTradingLanguages() != null) {
            if (postData.getLanguageDescriptions() != null) {
                for (LanguageDescriptionDto ld : postData.getLanguageDescriptions()) {
                    boolean match = false;

                    for (TradingLanguage tl : provider.getTradingLanguages()) {
                        if (tl.getLanguageCode().equals(ld.getLanguageCode())) {
                            match = true;
                            break;
                        }
                    }

                    if (!match) {
                        throw new MeveoApiException(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, "Language " + ld.getLanguageCode() + " is not supported by the provider.");
                    }
                }

                // create cat messages
                for (LanguageDescriptionDto ld : postData.getLanguageDescriptions()) {
                    CatMessages catMsg = catMessagesService.getCatMessages( chargeTemplate.getCode(),UsageChargeTemplate.class.getSimpleName() , ld.getLanguageCode(),provider);

                    if (catMsg != null) {
                        catMsg.setDescription(ld.getDescription());
                        catMessagesService.update(catMsg, currentUser);
                    } else {
                        CatMessages catMessages = new CatMessages(UsageChargeTemplate.class.getSimpleName() , chargeTemplate.getCode(), ld.getLanguageCode(),
                            ld.getDescription());
                        catMessagesService.create(catMessages, currentUser);
                    }
                }
            }
        }

        if (postData.getTriggeredEdrs() != null) {
            List<TriggeredEDRTemplate> edrTemplates = new ArrayList<TriggeredEDRTemplate>();

            for (TriggeredEdrTemplateDto triggeredEdrTemplateDto : postData.getTriggeredEdrs().getTriggeredEdr()) {
                TriggeredEDRTemplate triggeredEdrTemplate = triggeredEDRTemplateService.findByCode(triggeredEdrTemplateDto.getCode(), provider);
                if (triggeredEdrTemplate == null) {
                    throw new EntityDoesNotExistsException(TriggeredEDRTemplate.class, triggeredEdrTemplateDto.getCode());
                }

                edrTemplates.add(triggeredEdrTemplate);
            }

            chargeTemplate.setEdrTemplates(edrTemplates);
        }

        chargeTemplate = usageChargeTemplateService.update(chargeTemplate, currentUser);

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), chargeTemplate, false, currentUser);
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
        
        return chargeTemplate;
    }

    public UsageChargeTemplateDto find(String code, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("usageChargeTemplateCode");
            handleMissingParameters();
        }

        UsageChargeTemplateDto result = new UsageChargeTemplateDto();

        // check if code already exists
        UsageChargeTemplate chargeTemplate = usageChargeTemplateService.findByCode(code, currentUser.getProvider(), Arrays.asList("invoiceSubCategory"));
        if (chargeTemplate == null) {
            throw new EntityDoesNotExistsException(UsageChargeTemplateDto.class, code);
        }

        result = new UsageChargeTemplateDto(chargeTemplate, entityToDtoConverter.getCustomFieldsDTO(chargeTemplate));

        List<LanguageDescriptionDto> languageDescriptions = new ArrayList<LanguageDescriptionDto>();
        for (CatMessages msg : catMessagesService.getCatMessagesList(UsageChargeTemplate.class.getSimpleName() , chargeTemplate.getCode(),currentUser.getProvider())) {
            languageDescriptions.add(new LanguageDescriptionDto(msg.getLanguageCode(), msg.getDescription()));
        }

        result.setLanguageDescriptions(languageDescriptions);

        return result;
    }

    public void remove(String code, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("usageChargeTemplateCode");
            handleMissingParameters();
        }

        // check if code already exists
        UsageChargeTemplate chargeTemplate = usageChargeTemplateService.findByCode(code, currentUser.getProvider(), Arrays.asList("invoiceSubCategory"));
        if (chargeTemplate == null) {
            throw new EntityDoesNotExistsException(UsageChargeTemplateDto.class, code);
        }
        
        usageChargeTemplateService.remove(chargeTemplate, currentUser);
    }

    public UsageChargeTemplate createOrUpdate(UsageChargeTemplateDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (usageChargeTemplateService.findByCode(postData.getCode(), currentUser.getProvider()) == null) {
            return create(postData, currentUser);
        } else {
            return update(postData, currentUser);
        }
    }
}
