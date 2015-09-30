package org.meveo.api.catalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.api.dto.catalog.TriggeredEdrTemplateDto;
import org.meveo.api.dto.catalog.UsageChargeTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.TriggeredEDRTemplateService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class UsageChargeTemplateApi extends BaseApi {

	@Inject
	private UsageChargeTemplateService usageChargeTemplateService;

	@Inject
	private InvoiceSubCategoryService invoiceSubCategoryService;

	@Inject
	private CatMessagesService catMessagesService;

	@Inject
	private TriggeredEDRTemplateService triggeredEDRTemplateService;

	public void create(UsageChargeTemplateDto postData, User currentUser) throws MeveoApiException {
		
		validate(postData);
		
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription()) && !StringUtils.isBlank(postData.getInvoiceSubCategory())) {
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
							throw new MeveoApiException(MeveoApiErrorCode.GENERIC_API_EXCEPTION, "Language " + ld.getLanguageCode() + " is not supported by the provider.");
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
			
			// populate customFields
			if (postData.getCustomFields() != null) {
                try {
                    populateCustomFields(AccountLevelEnum.CHARGE, postData.getCustomFields().getCustomField(), chargeTemplate, currentUser);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					log.error("Failed to associate custom field instance to an entity", e);
					throw new MeveoApiException("Failed to associate custom field instance to an entity");
				}
			}

			usageChargeTemplateService.create(chargeTemplate, currentUser, provider);

			// create cat messages
			if (postData.getLanguageDescriptions() != null) {
				for (LanguageDescriptionDto ld : postData.getLanguageDescriptions()) {
					CatMessages catMessages = new CatMessages(UsageChargeTemplate.class.getSimpleName() + "_" + chargeTemplate.getId(), ld.getLanguageCode(), ld.getDescription());

					catMessagesService.create(catMessages, currentUser, provider);
				}
			}
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}
			if (StringUtils.isBlank(postData.getInvoiceSubCategory())) {
				missingParameters.add("invoiceSubCategory");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void update(UsageChargeTemplateDto postData, User currentUser) throws MeveoApiException {
		
		validate(postData);
		
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription()) && !StringUtils.isBlank(postData.getInvoiceSubCategory())) {
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
							throw new MeveoApiException(MeveoApiErrorCode.GENERIC_API_EXCEPTION, "Language " + ld.getLanguageCode() + " is not supported by the provider.");
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
							throw new MeveoApiException(MeveoApiErrorCode.GENERIC_API_EXCEPTION, "Language " + ld.getLanguageCode() + " is not supported by the provider.");
						}
					}

					// create cat messages
					for (LanguageDescriptionDto ld : postData.getLanguageDescriptions()) {
						CatMessages catMsg = catMessagesService.getCatMessages(UsageChargeTemplate.class.getSimpleName() + "_" + chargeTemplate.getId(), ld.getLanguageCode());

						if (catMsg != null) {
							catMsg.setDescription(ld.getDescription());
							catMessagesService.update(catMsg, currentUser);
						} else {
							CatMessages catMessages = new CatMessages(UsageChargeTemplate.class.getSimpleName() + "_" + chargeTemplate.getId(), ld.getLanguageCode(),
									ld.getDescription());
							catMessagesService.create(catMessages, currentUser, provider);
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
			
			// populate customFields
			if (postData.getCustomFields() != null) {
				try {
                    populateCustomFields(AccountLevelEnum.CHARGE, postData.getCustomFields().getCustomField(), chargeTemplate, currentUser);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					log.error("Failed to associate custom field instance to an entity", e);
					throw new MeveoApiException("Failed to associate custom field instance to an entity");
				}
			}

			usageChargeTemplateService.update(chargeTemplate, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}
			if (StringUtils.isBlank(postData.getInvoiceSubCategory())) {
				missingParameters.add("invoiceSubCategory");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public UsageChargeTemplateDto find(String code, Provider provider) throws MeveoApiException {
		UsageChargeTemplateDto result = new UsageChargeTemplateDto();

		if (!StringUtils.isBlank(code)) {
			// check if code already exists
			UsageChargeTemplate chargeTemplate = usageChargeTemplateService.findByCode(code, provider, Arrays.asList("invoiceSubCategory"));
			if (chargeTemplate == null) {
				throw new EntityDoesNotExistsException(UsageChargeTemplateDto.class, code);
			}

			result = new UsageChargeTemplateDto(chargeTemplate);

			List<LanguageDescriptionDto> languageDescriptions = new ArrayList<LanguageDescriptionDto>();
			for (CatMessages msg : catMessagesService.getCatMessagesList(UsageChargeTemplate.class.getSimpleName() + "_" + chargeTemplate.getId())) {
				languageDescriptions.add(new LanguageDescriptionDto(msg.getLanguageCode(), msg.getDescription()));
			}

			result.setLanguageDescriptions(languageDescriptions);
		} else {
			if (StringUtils.isBlank(code)) {
				missingParameters.add("usageChargeTemplateCode");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}

		return result;
	}

	public void remove(String code, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(code)) {
			// check if code already exists
			UsageChargeTemplate chargeTemplate = usageChargeTemplateService.findByCode(code, provider, Arrays.asList("invoiceSubCategory"));
			if (chargeTemplate == null) {
				throw new EntityDoesNotExistsException(UsageChargeTemplateDto.class, code);
			}

			// remove cat messages
			catMessagesService.batchRemove(UsageChargeTemplate.class.getSimpleName(), chargeTemplate.getId(), provider);

			usageChargeTemplateService.remove(chargeTemplate);
		} else {
			if (StringUtils.isBlank(code)) {
				missingParameters.add("usageChargeTemplateCode");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}
	
	public void createOrUpdate(UsageChargeTemplateDto postData, User currentUser) throws MeveoApiException {
		
		if (usageChargeTemplateService.findByCode(postData.getCode(), currentUser.getProvider()) == null) {
			create(postData, currentUser);
		} else {
			update(postData, currentUser);
		}
	}
}
