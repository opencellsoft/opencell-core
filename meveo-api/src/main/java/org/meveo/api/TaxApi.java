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

			taxService.create(tax, currentUser, provider);

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
						CatMessages catMsg = new CatMessages(tax.getClass()
								.getSimpleName() + "_" + tax.getId(),
								ld.getLanguageCode(), ld.getDescription());

						catMessagesService
								.create(catMsg, currentUser, provider);
					}
				}
			}
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(postData.getCode())) {
				missingFields.add("code");
			}

			if (StringUtils.isBlank(postData.getDescription())) {
				missingFields.add("description");
			}

			if (StringUtils.isBlank(postData.getPercent())) {
				missingFields.add("percent");
			}

			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MissingParameterException(sb.toString());
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

			taxService.update(tax, currentUser);

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
								tax.getClass().getSimpleName() + "_"
										+ tax.getId(), ld.getLanguageCode());

						if (catMsg != null) {
							catMsg.setDescription(ld.getDescription());
							catMessagesService.update(catMsg);
						} else {
							CatMessages catMessages = new CatMessages(tax
									.getClass().getSimpleName()
									+ "_"
									+ tax.getId(), ld.getLanguageCode(),
									ld.getDescription());
							catMessagesService.create(catMessages);
						}
					}
				}
			}
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(postData.getCode())) {
				missingFields.add("code");
			}

			if (StringUtils.isBlank(postData.getDescription())) {
				missingFields.add("description");
			}

			if (StringUtils.isBlank(postData.getPercent())) {
				missingFields.add("percent");
			}

			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MissingParameterException(sb.toString());
		}

		return result;
	}

	public TaxDto find(String taxCode, User currentUser)
			throws MeveoApiException {
		TaxDto result = new TaxDto();

		if (!StringUtils.isBlank(taxCode)) {
			Tax tax = taxService.findByCode(taxCode, currentUser.getProvider());
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

			return result;
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(taxCode)) {
				missingFields.add("code");
			}

			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MissingParameterException(sb.toString());
		}
	}

	public ActionStatus remove(String taxCode, User currentUser)
			throws MeveoApiException {
		ActionStatus result = new ActionStatus();

		if (!StringUtils.isBlank(taxCode)) {
			Tax tax = taxService.findByCode(taxCode, currentUser.getProvider());
			if (tax == null) {
				throw new EntityDoesNotExistsException(Tax.class, taxCode);
			}

			taxService.remove(tax);
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(taxCode)) {
				missingFields.add("code");
			}

			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MissingParameterException(sb.toString());
		}

		return result;
	}

}
