package org.meveo.api;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.InvoiceCategoryDto;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class InvoiceCategoryApi extends BaseApi {

	@Inject
	private InvoiceCategoryService invoiceCategoryService;

	@Inject
	private CatMessagesService catMessagesService;

	public void create(InvoiceCategoryDto postData, User currentUser)
			throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode())
				&& !StringUtils.isBlank(postData.getDescription())) {
			Provider provider = currentUser.getProvider();

			if (invoiceCategoryService.findByCode(postData.getCode(), provider) != null) {
				throw new EntityAlreadyExistsException(InvoiceCategory.class,
						postData.getCode());
			}

			InvoiceCategory invoiceCategory = new InvoiceCategory();
			invoiceCategory.setCode(postData.getCode());
			invoiceCategory.setDescription(postData.getDescription());

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

			invoiceCategoryService.create(invoiceCategory, currentUser,
					provider);

			// create cat messages
			if (postData.getLanguageDescriptions() != null) {
				for (LanguageDescriptionDto ld : postData
						.getLanguageDescriptions()) {
					CatMessages catMsg = new CatMessages(
							InvoiceCategory.class.getSimpleName() + "_"
									+ invoiceCategory.getId(),
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

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	public void update(InvoiceCategoryDto postData, User currentUser)
			throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode())
				&& !StringUtils.isBlank(postData.getDescription())) {
			Provider provider = currentUser.getProvider();

			InvoiceCategory invoiceCategory = invoiceCategoryService
					.findByCode(postData.getCode(), provider);
			if (invoiceCategory == null) {
				throw new EntityDoesNotExistsException(InvoiceCategory.class,
						postData.getCode());
			}

			invoiceCategory.setDescription(postData.getDescription());

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
								InvoiceCategory.class.getSimpleName() + "_"
										+ invoiceCategory.getId(),
								ld.getLanguageCode());

						if (catMsg != null) {
							catMsg.setDescription(ld.getDescription());
							catMessagesService.update(catMsg);
						} else {
							CatMessages catMessages = new CatMessages(
									InvoiceCategory.class.getSimpleName() + "_"
											+ invoiceCategory.getId(),
									ld.getLanguageCode(), ld.getDescription());
							catMessagesService.create(catMessages);
						}
					}
				}
			}

			invoiceCategoryService.update(invoiceCategory, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}

			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	public InvoiceCategoryDto find(String code, Provider provider)
			throws MeveoApiException {
		InvoiceCategoryDto result = new InvoiceCategoryDto();

		if (!StringUtils.isBlank(code)) {
			InvoiceCategory invoiceCategory = invoiceCategoryService
					.findByCode(code, provider);
			if (invoiceCategory == null) {
				throw new EntityDoesNotExistsException(InvoiceCategory.class,
						code);
			}

			result = new InvoiceCategoryDto(invoiceCategory);

			List<LanguageDescriptionDto> languageDescriptions = new ArrayList<LanguageDescriptionDto>();
			for (CatMessages msg : catMessagesService
					.getCatMessagesList(InvoiceCategory.class.getSimpleName()
							+ "_" + invoiceCategory.getId())) {
				languageDescriptions.add(new LanguageDescriptionDto(msg
						.getLanguageCode(), msg.getDescription()));
			}

			result.setLanguageDescriptions(languageDescriptions);
		} else {
			missingParameters.add("invoiceCategoryCode");

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}

		return result;
	}

	public void remove(String code, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(code)) {
			InvoiceCategory invoiceCategory = invoiceCategoryService
					.findByCode(code, provider);
			if (invoiceCategory == null) {
				throw new EntityDoesNotExistsException(InvoiceCategory.class,
						code);
			}

			// remove cat messages
			catMessagesService.batchRemove(
					InvoiceCategory.class.getSimpleName(),
					invoiceCategory.getId());

			invoiceCategoryService.remove(invoiceCategory);
		} else {
			missingParameters.add("invoiceCategoryCode");

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}
}
