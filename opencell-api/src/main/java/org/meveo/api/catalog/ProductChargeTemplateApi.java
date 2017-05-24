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
import org.meveo.api.dto.catalog.ProductChargeTemplateDto;
import org.meveo.api.dto.catalog.TriggeredEdrTemplateDto;
import org.meveo.api.dto.catalog.UsageChargeTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.catalog.ProductChargeTemplate;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.finance.RevenueRecognitionRule;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.ProductChargeTemplateService;
import org.meveo.service.catalog.impl.TriggeredEDRTemplateService;
import org.meveo.service.finance.RevenueRecognitionRuleService;

@Stateless
public class ProductChargeTemplateApi extends BaseCrudApi<ProductChargeTemplate, ProductChargeTemplateDto> {

	@Inject
	private ProductChargeTemplateService productChargeTemplateService;

	@Inject
	private InvoiceSubCategoryService invoiceSubCategoryService;

	@Inject
	private RevenueRecognitionRuleService revenueRecognitionRuleService;

	@Inject
	private TriggeredEDRTemplateService triggeredEDRTemplateService;

	@Inject
	private CatMessagesService catMessagesService;
	
    @Inject
    private TradingLanguageService tradingLanguageService;

	public ProductChargeTemplate create(ProductChargeTemplateDto postData) throws MeveoApiException, BusinessException {

		validate(postData);

		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		}
		if (StringUtils.isBlank(postData.getInvoiceSubCategory())) {
			missingParameters.add("invoiceSubCategory");
		}

		handleMissingParameters();

		

		// check if code already exists
		if (productChargeTemplateService.findByCode(postData.getCode()) != null) {
			throw new EntityAlreadyExistsException(ProductChargeTemplate.class, postData.getCode());
		}

		InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(postData.getInvoiceSubCategory());
		if (invoiceSubCategory == null) {
			throw new EntityDoesNotExistsException(InvoiceSubCategory.class, postData.getInvoiceSubCategory());
		}

		List<TradingLanguage> tradingLanguages = tradingLanguageService.list();
		if (!tradingLanguages.isEmpty() && postData.getLanguageDescriptions() != null) {
			if (postData.getLanguageDescriptions() != null) {
				for (LanguageDescriptionDto ld : postData.getLanguageDescriptions()) {
					boolean match = false;

					for (TradingLanguage tl : tradingLanguages) {
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

		ProductChargeTemplate chargeTemplate = new ProductChargeTemplate();
		chargeTemplate.setCode(postData.getCode());
		chargeTemplate.setDescription(postData.getDescription());
		chargeTemplate.setDisabled(postData.isDisabled());
		chargeTemplate.setAmountEditable(postData.getAmountEditable());
		chargeTemplate.setUnitMultiplicator(postData.getUnitMultiplicator());
		chargeTemplate.setRatingUnitDescription(postData.getRatingUnitDescription());
		chargeTemplate.setUnitNbDecimal(postData.getUnitNbDecimal());
		chargeTemplate.setInputUnitDescription(postData.getInputUnitDescription());
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

		if (postData.getRevenueRecognitionRuleCode() != null) {
			RevenueRecognitionRule revenueRecognitionRule = revenueRecognitionRuleService.findByCode(postData.getRevenueRecognitionRuleCode());
			chargeTemplate.setRevenueRecognitionRule(revenueRecognitionRule);
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

		// populate customFields
		try {
			populateCustomFields(postData.getCustomFields(), chargeTemplate, true);
        } catch (MissingParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        productChargeTemplateService.create(chargeTemplate);
        
		// create cat messages
		if (postData.getLanguageDescriptions() != null) {
			for (LanguageDescriptionDto ld : postData.getLanguageDescriptions()) {
				CatMessages catMessages = new CatMessages(chargeTemplate, ld.getLanguageCode(), ld.getDescription());

				catMessagesService.create(catMessages);
			}
		}
		
		return chargeTemplate;
	}

	public ProductChargeTemplate update(ProductChargeTemplateDto postData) throws MeveoApiException, BusinessException {

		validate(postData);

		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		}
		if (StringUtils.isBlank(postData.getInvoiceSubCategory())) {
			missingParameters.add("invoiceSubCategory");
		}

		handleMissingParameters();

		

		// check if code already exists
		ProductChargeTemplate chargeTemplate = productChargeTemplateService.findByCode(postData.getCode());
		if (chargeTemplate == null) {
			throw new EntityDoesNotExistsException(ProductChargeTemplate.class, postData.getCode());
		}

		InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(postData.getInvoiceSubCategory());
		if (invoiceSubCategory == null) {
			throw new EntityDoesNotExistsException(InvoiceSubCategory.class, postData.getInvoiceSubCategory());
		}

		List<TradingLanguage> tradingLanguages = tradingLanguageService.list();
        if (!tradingLanguages.isEmpty()) {
			if (postData.getLanguageDescriptions() != null) {
				for (LanguageDescriptionDto ld : postData.getLanguageDescriptions()) {
					boolean match = false;

					for (TradingLanguage tl : tradingLanguages) {
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
		chargeTemplate.setCode(StringUtils.isBlank(postData.getUpdatedCode())?postData.getCode():postData.getUpdatedCode());
		chargeTemplate.setDescription(postData.getDescription());
		chargeTemplate.setDisabled(postData.isDisabled());
		chargeTemplate.setAmountEditable(postData.getAmountEditable());
		chargeTemplate.setUnitMultiplicator(postData.getUnitMultiplicator());
		chargeTemplate.setRatingUnitDescription(postData.getRatingUnitDescription());
		chargeTemplate.setUnitNbDecimal(postData.getUnitNbDecimal());
		chargeTemplate.setInputUnitDescription(postData.getInputUnitDescription());
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

		if (postData.getRevenueRecognitionRuleCode() != null) {
			RevenueRecognitionRule revenueRecognitionRule = revenueRecognitionRuleService.findByCode(postData.getRevenueRecognitionRuleCode());
			chargeTemplate.setRevenueRecognitionRule(revenueRecognitionRule);
		}

        if (!tradingLanguages.isEmpty()) {
			if (postData.getLanguageDescriptions() != null) {
				for (LanguageDescriptionDto ld : postData.getLanguageDescriptions()) {
					boolean match = false;

					for (TradingLanguage tl : tradingLanguages) {
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
					CatMessages catMsg = catMessagesService.getCatMessages(chargeTemplate, ld.getLanguageCode());

					if (catMsg != null) {
						catMsg.setDescription(ld.getDescription());
						catMessagesService.update(catMsg);
					} else {
						CatMessages catMessages = new CatMessages(chargeTemplate, ld.getLanguageCode(),
								ld.getDescription());
						catMessagesService.create(catMessages);
					}
				}
			}
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

		// populate customFields
		try {
			populateCustomFields(postData.getCustomFields(), chargeTemplate, false);
        } catch (MissingParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        chargeTemplate = productChargeTemplateService.update(chargeTemplate);
		
		return chargeTemplate;
	}

    /* (non-Javadoc)
     * @see org.meveo.api.ApiService#find(java.lang.String)
     */
    @Override
	public ProductChargeTemplateDto find(String code) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {

		if (StringUtils.isBlank(code)) {
			missingParameters.add("usageChargeTemplateCode");
			handleMissingParameters();
		}

		// check if code already exists
		ProductChargeTemplate chargeTemplate = productChargeTemplateService.findByCode(code, Arrays.asList("invoiceSubCategory"));
		if (chargeTemplate == null) {
			throw new EntityDoesNotExistsException(UsageChargeTemplateDto.class, code);
		}

		ProductChargeTemplateDto result = new ProductChargeTemplateDto(chargeTemplate, entityToDtoConverter.getCustomFieldsDTO(chargeTemplate));

		List<LanguageDescriptionDto> languageDescriptions = new ArrayList<LanguageDescriptionDto>();
		for (CatMessages msg : catMessagesService.getCatMessagesList(chargeTemplate)) {
			languageDescriptions.add(new LanguageDescriptionDto(msg.getLanguageCode(), msg.getDescription()));
		}

		result.setLanguageDescriptions(languageDescriptions);

		return result;
	}

    /* (non-Javadoc)
     * @see org.meveo.api.ApiService#findIgnoreNotFound(java.lang.String)
     */
    @Override
    public ProductChargeTemplateDto findIgnoreNotFound(String code) throws MissingParameterException, InvalidParameterException, MeveoApiException {
        try {
            return find(code);
        } catch (EntityDoesNotExistsException e) {
            return null;
        }
    }

	public void remove(String code) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(code)) {
			missingParameters.add("productChargeTemplateCode");
			handleMissingParameters();
		}

		// check if code already exists
		ProductChargeTemplate chargeTemplate = productChargeTemplateService.findByCode(code, Arrays.asList("invoiceSubCategory"));
		if (chargeTemplate == null) {
			throw new EntityDoesNotExistsException(ProductChargeTemplate.class, code);
		}

		productChargeTemplateService.remove(chargeTemplate);
	}

	public ProductChargeTemplate createOrUpdate(ProductChargeTemplateDto postData) throws MeveoApiException, BusinessException {		
		if (productChargeTemplateService.findByCode(postData.getCode()) == null) {
			return create(postData);
		} else {
			return update(postData);
		}
	}
	
	public List<ProductChargeTemplateDto> list() {
		List<ProductChargeTemplate> listProductChargeTemplate = productChargeTemplateService.list();
		List<ProductChargeTemplateDto> dtos = new ArrayList<ProductChargeTemplateDto>();
		if(listProductChargeTemplate != null){
			for(ProductChargeTemplate productChargeTemplate : listProductChargeTemplate){
				dtos.add(new ProductChargeTemplateDto(productChargeTemplate, null));			
			}
		}
		return dtos;
	}
}