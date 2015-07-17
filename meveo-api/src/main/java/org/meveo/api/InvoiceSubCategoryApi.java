package org.meveo.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.InvoiceSubCategoryDto;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class InvoiceSubCategoryApi extends BaseApi {

	@Inject
	private InvoiceSubCategoryService invoiceSubCategoryService;

	@Inject
	private InvoiceCategoryService invoiceCategoryService;

	@Inject
	private CatMessagesService catMessagesService;

	public void create(InvoiceSubCategoryDto postData, User currentUser)
			throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getInvoiceCategory())
				&& !StringUtils.isBlank(postData.getCode())
				&& !StringUtils.isBlank(postData.getDescription())) {
			Provider provider = currentUser.getProvider();

			if (invoiceSubCategoryService.findByCode(postData.getCode(),
					provider) != null) {
				throw new EntityAlreadyExistsException(
						InvoiceSubCategory.class, postData.getCode());
			}

			// check if invoice cat exists
			InvoiceCategory invoiceCategory = invoiceCategoryService
					.findByCode(postData.getInvoiceCategory(), provider);
			if (invoiceCategory == null) {
				throw new EntityDoesNotExistsException(InvoiceCategory.class,
						postData.getInvoiceCategory());
			}

			InvoiceSubCategory invoiceSubCategory = new InvoiceSubCategory();
			invoiceSubCategory.setInvoiceCategory(invoiceCategory);
			invoiceSubCategory.setCode(postData.getCode());
			invoiceSubCategory.setDescription(postData.getDescription());

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

			invoiceSubCategoryService.create(invoiceSubCategory, currentUser,
					provider);

			// create cat messages
			if (postData.getLanguageDescriptions() != null) {
				for (LanguageDescriptionDto ld : postData
						.getLanguageDescriptions()) {
					CatMessages catMsg = new CatMessages(
							InvoiceSubCategory.class.getSimpleName() + "_"
									+ invoiceSubCategory.getId(),
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
			if (StringUtils.isBlank(postData.getInvoiceCategory())) {
				missingParameters.add("invoiceCategory");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	public void update(InvoiceSubCategoryDto postData, User currentUser)
			throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getInvoiceCategory())
				&& !StringUtils.isBlank(postData.getCode())
				&& !StringUtils.isBlank(postData.getDescription())) {
			Provider provider = currentUser.getProvider();

			InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService
					.findByCode(postData.getCode(), provider);
			if (invoiceSubCategory == null) {
				throw new EntityDoesNotExistsException(
						InvoiceSubCategory.class, postData.getCode());
			}

			// check if invoice cat exists
			InvoiceCategory invoiceCategory = invoiceCategoryService
					.findByCode(postData.getInvoiceCategory(), provider);
			if (invoiceCategory == null) {
				throw new EntityDoesNotExistsException(InvoiceCategory.class,
						postData.getInvoiceCategory());
			}

			invoiceSubCategory.setInvoiceCategory(invoiceCategory);
			invoiceSubCategory.setDescription(postData.getDescription());

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
						CatMessages catMsg = new CatMessages(
								InvoiceSubCategory.class.getSimpleName() + "_"
										+ invoiceSubCategory.getId(),
								ld.getLanguageCode(), ld.getDescription());

						catMessagesService
								.create(catMsg, currentUser, provider);
					}
				}
			}

			invoiceSubCategoryService.update(invoiceSubCategory, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}
			if (StringUtils.isBlank(postData.getInvoiceCategory())) {
				missingParameters.add("invoiceCategory");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	public InvoiceSubCategoryDto find(String code, Provider provider)
			throws MeveoApiException {
		InvoiceSubCategoryDto result = new InvoiceSubCategoryDto();

		if (!StringUtils.isBlank(code)) {
			InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService
					.findByCode(code, provider,
							Arrays.asList("invoiceCategory"));
			if (invoiceSubCategory == null) {
				throw new EntityDoesNotExistsException(
						InvoiceSubCategory.class, code);
			}

			result = new InvoiceSubCategoryDto(invoiceSubCategory);

			List<LanguageDescriptionDto> languageDescriptions = new ArrayList<LanguageDescriptionDto>();
			for (CatMessages msg : catMessagesService
					.getCatMessagesList(InvoiceSubCategory.class
							.getSimpleName() + "_" + invoiceSubCategory.getId())) {
				languageDescriptions.add(new LanguageDescriptionDto(msg
						.getLanguageCode(), msg.getDescription()));
			}

			result.setLanguageDescriptions(languageDescriptions);
		} else {
			missingParameters.add("invoiceSubCategoryCode");

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}

		return result;
	}

	public void remove(String code, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(code)) {
			InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService
					.findByCode(code, provider);
			if (invoiceSubCategory == null) {
				throw new EntityDoesNotExistsException(
						InvoiceSubCategory.class, code);
			}

			// remove cat messages
			catMessagesService.batchRemove(
					InvoiceSubCategory.class.getSimpleName(),
					invoiceSubCategory.getId(),provider);

			invoiceSubCategoryService.remove(invoiceSubCategory);
		} else {
			missingParameters.add("invoiceSubCategoryCode");

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

}
