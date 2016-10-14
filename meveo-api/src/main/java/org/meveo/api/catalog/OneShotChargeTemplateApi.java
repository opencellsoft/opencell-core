package org.meveo.api.catalog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.api.dto.catalog.OneShotChargeTemplateDto;
import org.meveo.api.dto.catalog.OneShotChargeTemplateWithPriceDto;
import org.meveo.api.dto.catalog.TriggeredEdrTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.finance.RevenueRecognitionRule;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.InvoiceSubCategoryCountryService;
import org.meveo.service.billing.impl.RealtimeChargingService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.TriggeredEDRTemplateService;
import org.meveo.service.finance.RevenueRecognitionRuleService;


@Stateless
public class OneShotChargeTemplateApi extends BaseCrudApi<OneShotChargeTemplate, OneShotChargeTemplateDto> {

    @Inject
    private OneShotChargeTemplateService oneShotChargeTemplateService;

    @Inject
    private InvoiceSubCategoryCountryService invoiceSubCategoryCountryService;

    @Inject
    private RealtimeChargingService realtimeChargingService;

    @Inject
    private SellerService sellerService;

    @Inject
    private TradingCurrencyService tradingCurrencyService;

    @Inject
    private TradingCountryService tradingCountryService;

    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    private CatMessagesService catMessagesService;

    @Inject
    private TriggeredEDRTemplateService triggeredEDRTemplateService;

    @Inject
    private RevenueRecognitionRuleService revenueRecognitionRuleService;

    public OneShotChargeTemplate create(OneShotChargeTemplateDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getInvoiceSubCategory())) {
            missingParameters.add("invoiceSubCategory");
        }
        if (StringUtils.isBlank(postData.getOneShotChargeTemplateType())) {
            missingParameters.add("oneShotChargeTemplateType");
        }

        handleMissingParameters();
        

        Provider provider = currentUser.getProvider();

        // check if code already exists
        if (oneShotChargeTemplateService.findByCode(postData.getCode(), provider) != null) {
            throw new EntityAlreadyExistsException(OneShotChargeTemplate.class, postData.getCode());
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

        OneShotChargeTemplate chargeTemplate = new OneShotChargeTemplate();
        chargeTemplate.setCode(postData.getCode());
        chargeTemplate.setDescription(postData.getDescription());
        chargeTemplate.setDisabled(postData.isDisabled());
        chargeTemplate.setAmountEditable(postData.getAmountEditable());
        chargeTemplate.setOneShotChargeTemplateType(postData.getOneShotChargeTemplateType());
        chargeTemplate.setInvoiceSubCategory(invoiceSubCategory);
        chargeTemplate.setImmediateInvoicing(postData.getImmediateInvoicing());
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
        	RevenueRecognitionRule revenueRecognitionScript = revenueRecognitionRuleService.findByCode(postData.getRevenueRecognitionRuleCode(), provider);
        	chargeTemplate.setRevenueRecognitionRule(revenueRecognitionScript);
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

        oneShotChargeTemplateService.create(chargeTemplate, currentUser);

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
                CatMessages catMsg = new CatMessages(OneShotChargeTemplate.class.getSimpleName() , chargeTemplate.getCode(), ld.getLanguageCode(), ld.getDescription());

                catMessagesService.create(catMsg, currentUser);
            }
        }
        
        return chargeTemplate;
    }

    public OneShotChargeTemplate update(OneShotChargeTemplateDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getInvoiceSubCategory())) {
            missingParameters.add("invoiceSubCategory");
        }
        if (StringUtils.isBlank(postData.getOneShotChargeTemplateType())) {
            missingParameters.add("oneShotChargeTemplateType");
        }

        handleMissingParameters();
        

        Provider provider = currentUser.getProvider();

        // check if code already exists
        OneShotChargeTemplate chargeTemplate = oneShotChargeTemplateService.findByCode(postData.getCode(), provider);
        if (chargeTemplate == null) {
            throw new EntityDoesNotExistsException(OneShotChargeTemplate.class, postData.getCode());
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

                // create cat messages
                for (LanguageDescriptionDto ld : postData.getLanguageDescriptions()) {
                    CatMessages catMsg = catMessagesService.getCatMessages( chargeTemplate.getCode(),OneShotChargeTemplate.class.getSimpleName(), ld.getLanguageCode(),provider);

                    if (catMsg != null) {
                        catMsg.setDescription(ld.getDescription());
                        catMessagesService.update(catMsg, currentUser);
                    } else {
                        CatMessages catMessages = new CatMessages(OneShotChargeTemplate.class.getSimpleName() , chargeTemplate.getCode(), ld.getLanguageCode(),
                            ld.getDescription());
                        catMessagesService.create(catMessages, currentUser);
                    }
                }
            }
        }

        chargeTemplate.setDescription(postData.getDescription());
        chargeTemplate.setDisabled(postData.isDisabled());
        chargeTemplate.setAmountEditable(postData.getAmountEditable());
        chargeTemplate.setOneShotChargeTemplateType(postData.getOneShotChargeTemplateType());
        chargeTemplate.setInvoiceSubCategory(invoiceSubCategory);
        chargeTemplate.setImmediateInvoicing(postData.getImmediateInvoicing());
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
        	RevenueRecognitionRule revenueRecognitionScript = revenueRecognitionRuleService.findByCode(postData.getRevenueRecognitionRuleCode(), provider);
        	chargeTemplate.setRevenueRecognitionRule(revenueRecognitionScript);
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

        chargeTemplate = oneShotChargeTemplateService.update(chargeTemplate, currentUser);

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), chargeTemplate, false, currentUser);
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
        
        return chargeTemplate;
    }

    public OneShotChargeTemplateDto find(String code, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("oneShotChargeTemplateCode");
            handleMissingParameters();
        }

        OneShotChargeTemplateDto result = new OneShotChargeTemplateDto();

        // check if code already exists
        OneShotChargeTemplate chargeTemplate = oneShotChargeTemplateService.findByCode(code, currentUser.getProvider(), Arrays.asList("invoiceSubCategory"));
        if (chargeTemplate == null) {
            throw new EntityDoesNotExistsException(OneShotChargeTemplate.class, code);
        }

        result = new OneShotChargeTemplateDto(chargeTemplate, entityToDtoConverter.getCustomFieldsDTO(chargeTemplate));

        List<LanguageDescriptionDto> languageDescriptions = new ArrayList<LanguageDescriptionDto>();
        for (CatMessages msg : catMessagesService.getCatMessagesList(OneShotChargeTemplate.class.getSimpleName() , chargeTemplate.getCode(),currentUser.getProvider())) {
            languageDescriptions.add(new LanguageDescriptionDto(msg.getLanguageCode(), msg.getDescription()));
        }

        result.setLanguageDescriptions(languageDescriptions);

        return result;
    }

    public void remove(String code, User currentUser) throws MeveoApiException, BusinessException  {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("oneShotChargeTemplateCode");
            handleMissingParameters();
        }
        // check if code already exists
        OneShotChargeTemplate chargeTemplate = oneShotChargeTemplateService.findByCode(code, currentUser.getProvider());
        if (chargeTemplate == null) {
            throw new EntityDoesNotExistsException(OneShotChargeTemplate.class, code);
        }

        oneShotChargeTemplateService.remove(chargeTemplate, currentUser);

    }

    public List<OneShotChargeTemplateWithPriceDto> listWithPrice(String languageCode, String countryCode, String currencyCode, String sellerCode, Date date, User currentUser)
            throws MeveoApiException {
        Provider provider = currentUser.getProvider();
        Seller seller = sellerService.findByCode(sellerCode, provider);
        TradingCurrency currency = tradingCurrencyService.findByTradingCurrencyCode(currencyCode, provider);
        TradingCountry country = tradingCountryService.findByTradingCountryCode(countryCode, provider);

        List<OneShotChargeTemplate> oneShotChargeTemplates = oneShotChargeTemplateService.getSubscriptionChargeTemplates(provider);
        List<OneShotChargeTemplateWithPriceDto> oneShotChargeTemplatesWPrice = new ArrayList<>();

        for (OneShotChargeTemplate oneShotChargeTemplate : oneShotChargeTemplates) {
            OneShotChargeTemplateWithPriceDto oneShotChargeDto = new OneShotChargeTemplateWithPriceDto();
            oneShotChargeDto.setChargeCode(oneShotChargeTemplate.getCode());
            oneShotChargeDto.setDescription(oneShotChargeTemplate.getDescription());
            InvoiceSubCategory invoiceSubCategory = oneShotChargeTemplate.getInvoiceSubCategory();

            if (country == null) {
                log.warn("country with code={} does not exists", countryCode);
            } else {
                InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), country.getId(),
                    provider);
                if (invoiceSubcategoryCountry != null && invoiceSubcategoryCountry.getTax() != null) {
                    Tax tax = invoiceSubcategoryCountry.getTax();
                    oneShotChargeDto.setTaxCode(tax.getCode());
                    oneShotChargeDto.setTaxDescription(tax.getDescription());
                    oneShotChargeDto.setTaxPercent(tax.getPercent() == null ? 0.0 : tax.getPercent().doubleValue());
                }
                try {
                    BigDecimal unitPrice = realtimeChargingService.getApplicationPrice(currentUser, seller, currency, country, oneShotChargeTemplate, date, null, BigDecimal.ONE,
                        null, null, null, true);
                    if (unitPrice != null) {
                        oneShotChargeDto.setUnitPriceWithoutTax(unitPrice.doubleValue());
                    }
                } catch (BusinessException e) {
                    log.warn("error occurred while getting application price", e);
                    throw new MeveoApiException(e.getMessage());
                }
            }

            oneShotChargeTemplatesWPrice.add(oneShotChargeDto);
        }

        return oneShotChargeTemplatesWPrice;
    }

    public OneShotChargeTemplate createOrUpdate(OneShotChargeTemplateDto postData, User currentUser) throws MeveoApiException, BusinessException {
        if (oneShotChargeTemplateService.findByCode(postData.getCode(), currentUser.getProvider()) == null) {
            return create(postData, currentUser);
        } else {
            return update(postData, currentUser);
        }
    }
}
