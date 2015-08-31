package org.meveo.api;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.api.dto.TaxDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.meveo.service.catalog.impl.TaxService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class TaxApi extends BaseApi {

	@Inject
	private TaxService taxService;

	@Inject
	private CatMessagesService catMessagesService;

	public ActionStatus create(TaxDto postData, User currentUser)
			throws MeveoApiException {
		ActionStatus result = new ActionStatus();

		if (!StringUtils.isBlank(postData.getCode())
				&& !StringUtils.isBlank(postData.getDescription())
				&& !StringUtils.isBlank(postData.getPercent())) {
			Provider provider = currentUser.getProvider();

			// check if tax exists
			if (taxService.findByCode(postData.getCode(), provider) != null) {
				throw new EntityAlreadyExistsException(Tax.class,
						postData.getCode());
			}

			Tax tax = new Tax();
			tax.setCode(postData.getCode());
			tax.setDescription(postData.getDescription());
			tax.setPercent(postData.getPercent());
			tax.setAccountingCode(postData.getAccountingCode());

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

			taxService.create(tax, currentUser, provider);

			// create cat messages
			if (postData.getLanguageDescriptions() != null) {
				for (LanguageDescriptionDto ld : postData
						.getLanguageDescriptions()) {
					CatMessages catMsg = new CatMessages(
							Tax.class.getSimpleName() + "_" + tax.getId(),
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

			if (StringUtils.isBlank(postData.getPercent())) {
				missingParameters.add("percent");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}

		return result;
	}

	public ActionStatus update(TaxDto postData, User currentUser)
			throws MeveoApiException {
		ActionStatus result = new ActionStatus();

		if (!StringUtils.isBlank(postData.getCode())
				&& !StringUtils.isBlank(postData.getDescription())
				&& !StringUtils.isBlank(postData.getPercent())) {
			Provider provider = currentUser.getProvider();

			// check if tax exists
			Tax tax = taxService.findByCode(postData.getCode(), provider);
			if (tax == null) {
				throw new EntityDoesNotExistsException(Tax.class,
						postData.getCode());
			}

			tax.setDescription(postData.getDescription());
			tax.setPercent(postData.getPercent());
			tax.setAccountingCode(postData.getAccountingCode());

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
								Tax.class.getSimpleName() + "_" + tax.getId(),
								ld.getLanguageCode());

						if (catMsg != null) {
							catMsg.setDescription(ld.getDescription());
							catMessagesService.update(catMsg, currentUser);
						} else {
							CatMessages catMessages = new CatMessages(
									Tax.class.getSimpleName() + "_"
											+ tax.getId(),
									ld.getLanguageCode(), ld.getDescription());
							catMessagesService.create(catMessages, currentUser,
									provider);
						}
					}
				}
			}

			taxService.update(tax, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}

			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}

			if (StringUtils.isBlank(postData.getPercent())) {
				missingParameters.add("percent");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}

		return result;
	}

	public TaxDto find(String taxCode, Provider provider)
			throws MeveoApiException {
		TaxDto result = new TaxDto();

		if (!StringUtils.isBlank(taxCode)) {
			Tax tax = taxService.findByCode(taxCode, provider);
			if (tax == null) {
				throw new EntityDoesNotExistsException(Tax.class, taxCode);
			}

			result = new TaxDto(tax);

			List<LanguageDescriptionDto> languageDescriptions = new ArrayList<LanguageDescriptionDto>();
			for (CatMessages msg : catMessagesService
					.getCatMessagesList(Tax.class.getSimpleName() + "_"
							+ tax.getId())) {
				languageDescriptions.add(new LanguageDescriptionDto(msg
						.getLanguageCode(), msg.getDescription()));
			}

			result.setLanguageDescriptions(languageDescriptions);
		} else {
			if (StringUtils.isBlank(taxCode)) {
				missingParameters.add("code");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}

		return result;
	}

	public ActionStatus remove(String taxCode, Provider provider)
			throws MeveoApiException {
		ActionStatus result = new ActionStatus();

		if (!StringUtils.isBlank(taxCode)) {
			Tax tax = taxService.findByCode(taxCode, provider);
			if (tax == null) {
				throw new EntityDoesNotExistsException(Tax.class, taxCode);
			}

			// remove cat messages
			catMessagesService.batchRemove(Tax.class.getSimpleName(),
					tax.getId(),provider);

			taxService.remove(tax);
		} else {
			if (StringUtils.isBlank(taxCode)) {
				missingParameters.add("code");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}

		return result;
	}

	public void createOrUpdate(TaxDto postData, User currentUser) throws MeveoApiException {
		Tax tax = taxService.findByCode(postData.getCode(), currentUser.getProvider());
		
		if (tax == null) {
			create(postData, currentUser);
		} else {
			update(postData, currentUser);
		}
	}
}
