package org.meveo.api.catalog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.api.dto.catalog.OneShotChargeTemplateDto;
import org.meveo.api.dto.catalog.OneShotChargeTemplateWithPriceDto;
import org.meveo.api.dto.catalog.OneShotChargeTemplateWithPriceListDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
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
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.InvoiceSubCategoryCountryService;
import org.meveo.service.billing.impl.RealtimeChargingService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.util.MeveoJpaForJobs;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class OneShotChargeTemplateApi extends BaseApi {

	@Inject
	private Logger log;

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
	@MeveoJpaForJobs
	private EntityManager em;

	public void create(OneShotChargeTemplateDto postData, User currentUser)
			throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode())
				&& !StringUtils.isBlank(postData.getDescription())
				&& !StringUtils.isBlank(postData.getInvoiceSubCategory())
				&& !StringUtils
						.isBlank(postData.getOneShotChargeTemplateType())) {
			Provider provider = currentUser.getProvider();

			// check if code already exists
			if (oneShotChargeTemplateService.findByCode(postData.getCode(),
					provider) != null) {
				throw new EntityAlreadyExistsException(
						OneShotChargeTemplate.class, postData.getCode());
			}

			InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService
					.findByCode(postData.getInvoiceSubCategory(), provider);
			if (invoiceSubCategory == null) {
				throw new EntityDoesNotExistsException(
						InvoiceSubCategory.class,
						postData.getInvoiceSubCategory());
			}

			if (provider.getTradingLanguages() != null) {
				if (postData.getLanguageDescriptions() != null) {
					for (LanguageDescriptionDto ld : postData
							.getLanguageDescriptions()) {
						boolean match = false;

						for (TradingLanguage tl : provider
								.getTradingLanguages()) {
							if (tl.getLanguageCode().equals(
									ld.getLanguageCode())) {
								match = true;
								break;
							}
						}

						if (!match) {
							throw new MeveoApiException(
									MeveoApiErrorCode.GENERIC_API_EXCEPTION,
									"Language "
											+ ld.getLanguageCode()
											+ " is not supported by the provider.");
						}
					}
				}
			}

			OneShotChargeTemplate chargeTemplate = new OneShotChargeTemplate();
			chargeTemplate.setCode(postData.getCode());
			chargeTemplate.setDescription(postData.getDescription());
			chargeTemplate.setDisabled(postData.isDisabled());
			chargeTemplate.setAmountEditable(postData.getAmountEditable());
			chargeTemplate
					.setOneShotChargeTemplateType(OneShotChargeTemplateTypeEnum
							.getValue(postData.getOneShotChargeTemplateType()));
			chargeTemplate.setInvoiceSubCategory(invoiceSubCategory);
			chargeTemplate.setImmediateInvoicing(postData
					.getImmediateInvoicing());
			oneShotChargeTemplateService.create(chargeTemplate, currentUser,
					provider);

			// create cat messages
			if (postData.getLanguageDescriptions() != null) {
				for (LanguageDescriptionDto ld : postData
						.getLanguageDescriptions()) {
					CatMessages catMsg = new CatMessages(
							ChargeTemplate.class.getSimpleName() + "_"
									+ chargeTemplate.getId(),
							ld.getLanguageCode(), ld.getDescription());

					catMessagesService.create(catMsg, currentUser, provider);
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
			if (StringUtils.isBlank(postData.getOneShotChargeTemplateType())) {
				missingParameters.add("oneShotChargeTemplateType");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	public void update(OneShotChargeTemplateDto postData, User currentUser)
			throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode())
				&& !StringUtils.isBlank(postData.getDescription())
				&& !StringUtils.isBlank(postData.getInvoiceSubCategory())
				&& !StringUtils
						.isBlank(postData.getOneShotChargeTemplateType())) {
			Provider provider = currentUser.getProvider();

			// check if code already exists
			OneShotChargeTemplate chargeTemplate = oneShotChargeTemplateService
					.findByCode(postData.getCode(), provider);
			if (chargeTemplate == null) {
				throw new EntityDoesNotExistsException(
						OneShotChargeTemplate.class, postData.getCode());
			}

			InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService
					.findByCode(postData.getInvoiceSubCategory(), provider);
			if (invoiceSubCategory == null) {
				throw new EntityDoesNotExistsException(
						InvoiceSubCategory.class,
						postData.getInvoiceSubCategory());
			}

			if (provider.getTradingLanguages() != null) {
				if (postData.getLanguageDescriptions() != null) {
					for (LanguageDescriptionDto ld : postData
							.getLanguageDescriptions()) {
						boolean match = false;

						for (TradingLanguage tl : provider
								.getTradingLanguages()) {
							if (tl.getLanguageCode().equals(
									ld.getLanguageCode())) {
								match = true;
								break;
							}
						}

						if (!match) {
							throw new MeveoApiException(
									MeveoApiErrorCode.GENERIC_API_EXCEPTION,
									"Language "
											+ ld.getLanguageCode()
											+ " is not supported by the provider.");
						}
					}

					// create cat messages
					for (LanguageDescriptionDto ld : postData
							.getLanguageDescriptions()) {
						CatMessages catMsg = catMessagesService.getCatMessages(
								ChargeTemplate.class.getSimpleName() + "_"
										+ chargeTemplate.getId(),
								ld.getLanguageCode());

						if (catMsg != null) {
							catMsg.setDescription(ld.getDescription());
							catMessagesService.update(catMsg, currentUser);
						} else {
							CatMessages catMessages = new CatMessages(
									ChargeTemplate.class.getSimpleName() + "_"
											+ chargeTemplate.getId(),
									ld.getLanguageCode(), ld.getDescription());
							catMessagesService.create(catMessages, currentUser,
									provider);
						}
					}
				}
			}

			chargeTemplate.setDescription(postData.getDescription());
			chargeTemplate.setDisabled(postData.isDisabled());
			chargeTemplate.setAmountEditable(postData.getAmountEditable());
			chargeTemplate
					.setOneShotChargeTemplateType(OneShotChargeTemplateTypeEnum
							.getValue(postData.getOneShotChargeTemplateType()));
			chargeTemplate.setInvoiceSubCategory(invoiceSubCategory);
			chargeTemplate.setImmediateInvoicing(postData
					.getImmediateInvoicing());
			oneShotChargeTemplateService.update(chargeTemplate, currentUser);
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
			if (StringUtils.isBlank(postData.getOneShotChargeTemplateType())) {
				missingParameters.add("oneShotChargeTemplateType");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	public OneShotChargeTemplateDto find(String code, Provider provider)
			throws MeveoApiException {
		OneShotChargeTemplateDto result = new OneShotChargeTemplateDto();

		if (!StringUtils.isBlank(code)) {
			// check if code already exists
			OneShotChargeTemplate chargeTemplate = oneShotChargeTemplateService
					.findByCode(code, provider,
							Arrays.asList("invoiceSubCategory"));
			if (chargeTemplate == null) {
				throw new EntityDoesNotExistsException(
						OneShotChargeTemplate.class, code);
			}

			result = new OneShotChargeTemplateDto(chargeTemplate);

			List<LanguageDescriptionDto> languageDescriptions = new ArrayList<LanguageDescriptionDto>();
			for (CatMessages msg : catMessagesService
					.getCatMessagesList(ChargeTemplate.class.getSimpleName()
							+ "_" + chargeTemplate.getId())) {
				languageDescriptions.add(new LanguageDescriptionDto(msg
						.getLanguageCode(), msg.getDescription()));
			}

			result.setLanguageDescriptions(languageDescriptions);
		} else {
			if (StringUtils.isBlank(code)) {
				missingParameters.add("oneShotChargeTemplateCode");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}

		return result;
	}

	public void remove(String code, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(code)) {
			// check if code already exists
			OneShotChargeTemplate chargeTemplate = oneShotChargeTemplateService
					.findByCode(code, provider);
			if (chargeTemplate == null) {
				throw new EntityDoesNotExistsException(
						OneShotChargeTemplate.class, code);
			}

			// remove cat messages
			catMessagesService.batchRemove(
					OneShotChargeTemplate.class.getSimpleName(),
					chargeTemplate.getId(),provider);

			oneShotChargeTemplateService.remove(chargeTemplate);
		} else {
			if (StringUtils.isBlank(code)) {
				missingParameters.add("oneShotChargeTemplateCode");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	public OneShotChargeTemplateWithPriceListDto listWithPrice(
			String languageCode, String countryCode, String currencyCode,
			String sellerCode, Date date, User currentUser) {
		Provider provider = currentUser.getProvider();
		Seller seller = sellerService.findByCode(sellerCode, provider);
		TradingCurrency currency = tradingCurrencyService
				.findByTradingCurrencyCode(currencyCode, provider);
		TradingCountry country = tradingCountryService
				.findByTradingCountryCode(countryCode, provider);

		List<OneShotChargeTemplate> oneShotChargeTemplates = oneShotChargeTemplateService
				.getSubscriptionChargeTemplates(provider);
		OneShotChargeTemplateWithPriceListDto oneShotChargeTemplateListDto = new OneShotChargeTemplateWithPriceListDto();

		for (OneShotChargeTemplate oneShotChargeTemplate : oneShotChargeTemplates) {
			OneShotChargeTemplateWithPriceDto oneShotChargeDto = new OneShotChargeTemplateWithPriceDto();
			oneShotChargeDto.setChargeCode(oneShotChargeTemplate.getCode());
			oneShotChargeDto.setDescription(oneShotChargeTemplate
					.getDescription());
			InvoiceSubCategory invoiceSubCategory = oneShotChargeTemplate
					.getInvoiceSubCategory();

			if (country == null) {
				log.warn("country with code={} does not exists", countryCode);
			} else {
				InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService
						.findInvoiceSubCategoryCountry(em,
								invoiceSubCategory.getId(), country.getId(),
								provider);
				if (invoiceSubcategoryCountry != null
						&& invoiceSubcategoryCountry.getTax() != null) {
					Tax tax = invoiceSubcategoryCountry.getTax();
					oneShotChargeDto.setTaxCode(tax.getCode());
					oneShotChargeDto.setTaxDescription(tax.getDescription());
					oneShotChargeDto
							.setTaxPercent(tax.getPercent() == null ? 0.0 : tax
									.getPercent().doubleValue());
				}
				try {
					BigDecimal unitPrice = realtimeChargingService
							.getApplicationPrice(em, provider, seller,
									currency, country, oneShotChargeTemplate,
									date, null, BigDecimal.ONE, null, null,
									null, true);
					if (unitPrice != null) {
						oneShotChargeDto.setUnitPriceWithoutTax(unitPrice
								.doubleValue());
					}
				} catch (BusinessException e) {
					log.warn(e.getMessage());
				}
			}

			oneShotChargeTemplateListDto.getOneShotChargeTemplateDtos().add(
					oneShotChargeDto);
		}

		return oneShotChargeTemplateListDto;
	}

}
