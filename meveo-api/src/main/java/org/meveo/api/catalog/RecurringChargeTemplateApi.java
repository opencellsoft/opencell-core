package org.meveo.api.catalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.api.dto.catalog.RecurringChargeTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.LevelEnum;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class RecurringChargeTemplateApi extends ChargeTemplateApi {

	@Inject
	private RecurringChargeTemplateService recurringChargeTemplateService;

	@Inject
	private InvoiceSubCategoryService invoiceSubCategoryService;

	@Inject
	private CalendarService calendarService;

	@Inject
	private CatMessagesService catMessagesService;

	public void create(RecurringChargeTemplateDto postData, User currentUser)
			throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode())
				&& !StringUtils.isBlank(postData.getDescription())
				&& !StringUtils.isBlank(postData.getInvoiceSubCategory())
				&& !StringUtils.isBlank(postData.getCalendar())) {
			Provider provider = currentUser.getProvider();
			// check if code already exists
			if (recurringChargeTemplateService.findByCode(postData.getCode(),
					provider) != null) {
				throw new EntityAlreadyExistsException(
						RecurringChargeTemplate.class, postData.getCode());
			}

			InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService
					.findByCode(postData.getInvoiceSubCategory(), provider);
			if (invoiceSubCategory == null) {
				throw new EntityDoesNotExistsException(
						InvoiceSubCategory.class,
						postData.getInvoiceSubCategory());
			}

			Calendar calendar = calendarService.findByName(
					postData.getCalendar(), provider);
			if (calendar == null) {
				throw new EntityDoesNotExistsException(Calendar.class,
						postData.getCalendar());
			}

			RecurringChargeTemplate recurringChargeTemplate = new RecurringChargeTemplate();
			recurringChargeTemplate.setCode(postData.getCode());
			recurringChargeTemplate.setDescription(postData.getDescription());
			recurringChargeTemplate.setDisabled(postData.isDisabled());
			recurringChargeTemplate.setAmountEditable(postData
					.getAmountEditable());
			recurringChargeTemplate.setDurationTermInMonth(postData
					.getDurationTermInMonth());
			recurringChargeTemplate.setSubscriptionProrata(postData
					.getSubscriptionProrata());
			recurringChargeTemplate.setTerminationProrata(postData
					.getTerminationProrata());
			recurringChargeTemplate.setApplyInAdvance(postData
					.getApplyInAdvance());
			recurringChargeTemplate.setShareLevel(LevelEnum.getValue(postData
					.getShareLevel()));
			recurringChargeTemplate.setInvoiceSubCategory(invoiceSubCategory);
			recurringChargeTemplate.setCalendar(calendar);

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

			recurringChargeTemplateService.create(recurringChargeTemplate,
					currentUser, provider);

			// create cat messages
			if (postData.getLanguageDescriptions() != null) {
				for (LanguageDescriptionDto ld : postData
						.getLanguageDescriptions()) {
					CatMessages catMsg = new CatMessages(
							RecurringChargeTemplate.class.getSimpleName() + "_"
									+ recurringChargeTemplate.getId(),
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
			if (StringUtils.isBlank(postData.getCalendar())) {
				missingParameters.add("calendar");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	public void update(RecurringChargeTemplateDto postData, User currentUser)
			throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode())
				&& !StringUtils.isBlank(postData.getDescription())
				&& !StringUtils.isBlank(postData.getInvoiceSubCategory())
				&& !StringUtils.isBlank(postData.getCalendar())) {
			Provider provider = currentUser.getProvider();
			// check if code already exists
			RecurringChargeTemplate recurringChargeTemplate = recurringChargeTemplateService
					.findByCode(postData.getCode(), provider);
			if (recurringChargeTemplate == null) {
				throw new EntityDoesNotExistsException(
						RecurringChargeTemplate.class, postData.getCode());
			}

			InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService
					.findByCode(postData.getInvoiceSubCategory(), provider);
			if (invoiceSubCategory == null) {
				throw new EntityDoesNotExistsException(
						InvoiceSubCategory.class,
						postData.getInvoiceSubCategory());
			}

			Calendar calendar = calendarService.findByName(
					postData.getCalendar(), provider);
			if (calendar == null) {
				throw new EntityDoesNotExistsException(Calendar.class,
						postData.getCalendar());
			}

			recurringChargeTemplate.setDescription(postData.getDescription());
			recurringChargeTemplate.setDisabled(postData.isDisabled());
			recurringChargeTemplate.setAmountEditable(postData
					.getAmountEditable());
			recurringChargeTemplate.setDurationTermInMonth(postData
					.getDurationTermInMonth());
			recurringChargeTemplate.setSubscriptionProrata(postData
					.getSubscriptionProrata());
			recurringChargeTemplate.setTerminationProrata(postData
					.getTerminationProrata());
			recurringChargeTemplate.setApplyInAdvance(postData
					.getApplyInAdvance());
			recurringChargeTemplate.setShareLevel(LevelEnum.getValue(postData
					.getShareLevel()));
			recurringChargeTemplate.setInvoiceSubCategory(invoiceSubCategory);
			recurringChargeTemplate.setCalendar(calendar);

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
						CatMessages catMsg = catMessagesService
								.getCatMessages(
										RecurringChargeTemplate.class
												.getSimpleName()
												+ "_"
												+ recurringChargeTemplate
														.getId(), ld
												.getLanguageCode());

						if (catMsg != null) {
							catMsg.setDescription(ld.getDescription());
							catMessagesService.update(catMsg);
						} else {
							CatMessages catMessages = new CatMessages(
									RecurringChargeTemplate.class.getSimpleName()
											+ "_"
											+ recurringChargeTemplate.getId(),
									ld.getLanguageCode(), ld.getDescription());
							catMessagesService.create(catMessages);
						}
					}
				}
			}

			recurringChargeTemplateService.update(recurringChargeTemplate,
					currentUser);
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
			if (StringUtils.isBlank(postData.getCalendar())) {
				missingParameters.add("calendar");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	public RecurringChargeTemplateDto find(String code, Provider provider)
			throws MeveoApiException {
		RecurringChargeTemplateDto result = new RecurringChargeTemplateDto();

		if (!StringUtils.isBlank(code)) {
			// check if code already exists
			RecurringChargeTemplate recurringChargeTemplate = recurringChargeTemplateService
					.findByCode(code, provider,
							Arrays.asList("invoiceSubCategory", "calendar"));
			if (recurringChargeTemplate == null) {
				throw new EntityDoesNotExistsException(
						RecurringChargeTemplate.class, code);
			}

			result = new RecurringChargeTemplateDto(recurringChargeTemplate);

			List<LanguageDescriptionDto> languageDescriptions = new ArrayList<LanguageDescriptionDto>();
			for (CatMessages msg : catMessagesService
					.getCatMessagesList(RecurringChargeTemplate.class
							.getSimpleName()
							+ "_"
							+ recurringChargeTemplate.getId())) {
				languageDescriptions.add(new LanguageDescriptionDto(msg
						.getLanguageCode(), msg.getDescription()));
			}

			result.setLanguageDescriptions(languageDescriptions);
		} else {
			if (StringUtils.isBlank(code)) {
				missingParameters.add("recurringChargeTemplateCode");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}

		return result;
	}

	public void remove(String code, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(code)) {
			// check if code already exists
			RecurringChargeTemplate recurringChargeTemplate = recurringChargeTemplateService
					.findByCode(code, provider);
			if (recurringChargeTemplate == null) {
				throw new EntityDoesNotExistsException(
						RecurringChargeTemplate.class, code);
			}

			// remove cat messages
			catMessagesService.batchRemove(
					RecurringChargeTemplate.class.getSimpleName(),
					recurringChargeTemplate.getId());

			recurringChargeTemplateService.remove(recurringChargeTemplate);
		} else {
			if (StringUtils.isBlank(code)) {
				missingParameters.add("recurringChargeTemplateCode");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}
}
