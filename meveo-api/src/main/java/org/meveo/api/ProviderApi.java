package org.meveo.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.ProviderDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.rest.security.WSUser;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.User;
import org.meveo.model.billing.Country;
import org.meveo.model.billing.Language;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.LanguageService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.crm.impl.ProviderService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class ProviderApi extends BaseApi {

	@Inject
	private ProviderService providerService;

	@Inject
	@WSUser
	private User currentUser;

	@Inject
	private CountryService countryService;

	@Inject
	private CurrencyService currencyService;

	@Inject
	private LanguageService languageService;

	@Inject
	private UserAccountService userAccountService;

	public void create(ProviderDto postData, User currentUser)
			throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode())) {
			Provider provider = new Provider();
			provider.setCode(postData.getCode().toUpperCase());
			provider.setDescription(postData.getDescription());

			provider.setMulticountryFlag(postData.isMultiCountry());
			provider.setMulticurrencyFlag(postData.isMultiCurrency());
			provider.setMultilanguageFlag(postData.isMultiLanguage());

			// search for country
			if (!StringUtils.isBlank(postData.getCountry())) {
				Country country = countryService.findByCode(postData
						.getCountry());
				if (country == null) {
					throw new EntityDoesNotExistsException(
							Country.class.getName(), postData.getCountry());
				}

				provider.setCountry(country);
			}

			// search for currency
			if (!StringUtils.isBlank(postData.getCurrency())) {
				Currency currency = currencyService.findByCode(postData
						.getCurrency());
				if (currency == null) {
					throw new EntityDoesNotExistsException(
							Currency.class.getName(), postData.getCurrency());
				}

				provider.setCurrency(currency);
			}

			// search for language
			if (!StringUtils.isBlank(postData.getLanguage())) {
				Language language = languageService.findByCode(postData
						.getLanguage());
				if (language == null) {
					throw new EntityDoesNotExistsException(
							Language.class.getName(), postData.getLanguage());
				}

				provider.setLanguage(language);
			}

			if (!StringUtils.isBlank(postData.getUserAccount())) {
				UserAccount ua = userAccountService.findByCode(
						postData.getUserAccount(), currentUser.getProvider());
				provider.setUserAccount(ua);
			}

			// check if provider already exists
			if (providerService.findByCode(postData.getCode()) != null) {
				throw new EntityAlreadyExistsException(Provider.class,
						postData.getCode());
			}

			providerService.create(provider, currentUser);
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(postData.getCode())) {
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

	public ProviderDto find(String providerCode) throws MeveoApiException {
		if (!StringUtils.isBlank(providerCode)) {
			Provider provider = providerService.findByCodeWithFetch(
					providerCode,
					Arrays.asList("currency", "country", "language"));
			if (provider != null) {
				return new ProviderDto(provider);
			}

			throw new EntityDoesNotExistsException(Country.class, providerCode);
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(providerCode)) {
				missingFields.add("providerCode");
			}
			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MeveoApiException(sb.toString());
		}
	}

	public void update(ProviderDto postData, User currentUser)
			throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode())) {
			// search for provider
			Provider provider = providerService.findByCode(postData.getCode());

			provider.setDescription(postData.getDescription());

			provider.setMulticountryFlag(postData.isMultiCountry());
			provider.setMulticurrencyFlag(postData.isMultiCurrency());
			provider.setMultilanguageFlag(postData.isMultiLanguage());

			// search for country
			if (!StringUtils.isBlank(postData.getCountry())) {
				Country country = countryService.findByCode(postData
						.getCountry());
				if (country == null) {
					throw new EntityDoesNotExistsException(
							Country.class.getName(), postData.getCountry());
				}

				provider.setCountry(country);
			}

			// search for currency
			if (!StringUtils.isBlank(postData.getCurrency())) {
				Currency currency = currencyService.findByCode(postData
						.getCurrency());
				if (currency == null) {
					throw new EntityDoesNotExistsException(
							Currency.class.getName(), postData.getCurrency());
				}

				provider.setCurrency(currency);
			}

			// search for language
			if (!StringUtils.isBlank(postData.getLanguage())) {
				Language language = languageService.findByCode(postData
						.getLanguage());
				if (language == null) {
					throw new EntityDoesNotExistsException(
							Language.class.getName(), postData.getLanguage());
				}

				provider.setLanguage(language);
			}

			if (!StringUtils.isBlank(postData.getUserAccount())) {
				UserAccount ua = userAccountService.findByCode(
						postData.getUserAccount(), currentUser.getProvider());
				provider.setUserAccount(ua);
			}

			providerService.update(provider, currentUser);
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(postData.getCode())) {
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

}
