package org.meveo.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.meveo.api.dto.CountryDto;
import org.meveo.api.exception.EnvironmentException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.Auditable;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.User;
import org.meveo.model.billing.Country;
import org.meveo.model.billing.Language;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.LanguageService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.util.MeveoJpaForJobs;
import org.meveo.util.MeveoParamBean;

/**
 * @author Edward P. Legaspi
 * @since Oct 4, 2013
 **/
@Stateless
public class CountryServiceApi {

	@Inject
	private CountryService countryService;

	@Inject
	private TradingCountryService tradingCountryService;

	@Inject
	private CurrencyService currencyService;

	@Inject
	private LanguageService languageService;

	@Inject
	private ProviderService providerService;

	@Inject
	private UserService userService;

	@Inject
	private TradingCurrencyService tradingCurrencyService;

	@Inject
	@MeveoParamBean
	private ParamBean paramBean;

	@Inject
	@MeveoJpaForJobs
	private EntityManager em;

	public void create(CountryDto countryDto) throws EnvironmentException {
		if (!StringUtils.isBlank(countryDto.getCountryCode())
				&& !StringUtils.isBlank(countryDto.getName())
				&& !StringUtils.isBlank(countryDto.getCurrencyCode())) {

			// If countryCode exist in the trading country table
			// ("billing_trading_country"), return error.
			Provider provider = providerService.findById(countryDto
					.getProviderId());
			User currentUser = userService.findById(countryDto.getUserId());
			TradingCountry tradingCountry = tradingCountryService
					.findByTradingCountryCode(countryDto.getCountryCode(),
							provider);

			if (StringUtils.isBlank(countryDto.getCurrencyCode())) {
				throw new EnvironmentException("Currency code="
						+ countryDto.getCurrencyCode() + " does not exists.");
			}

			if (tradingCountry == null) {
				// check currency
				Country country = new Country();

				country = countryService
						.findByCode(countryDto.getCountryCode());
				Currency currency = null;
				Auditable auditable = new Auditable();

				// If country code doesn't exist in the reference table, create
				// the country in this table ("adm_country") with the currency
				// code for the default provider.
				if (country == null) {
					// If country code don't exist in the reference table,
					// create the country in this table ("adm_country") with the
					// currency code for the default provider.
					country = new Country();
					auditable.setCreated(new Date());
					auditable.setCreator(currentUser);
					country.setDescriptionEn(countryDto.getName());
					country.setCountryCode(countryDto.getCountryCode());
				} else {
					// If country code exist in the reference table but the
					// currencyCode values in the two tables are different,
					// change the value of Currencycode in the country reference
					auditable.setUpdated(new Date());
					auditable.setUpdater(currentUser);
				}
				country.setAuditable(auditable);

				if (countryDto.getCurrencyCode() != null) { //
					currency = currencyService.findByCode(countryDto
							.getCurrencyCode());
					// If currencyCode don't exist in reference table
					// ("adm_currency"), return error.
					if (currency == null) {
						throw new EnvironmentException("Currency code="
								+ countryDto.getCurrencyCode()
								+ " does not exists.");
					}
				} else {
					if (provider.getCurrency() != null) {
						currency = provider.getCurrency();
					}
				}
				country.setCurrency(currency);

				if (country.isTransient()) {
					countryService.create(em, country, currentUser, provider);
				} else {
					// countryService.update(em, country, currentUser);
				}

				Auditable auditableTrading = new Auditable();
				auditableTrading.setCreated(new Date());
				auditableTrading.setCreator(currentUser);

				// If country don't exist in the trading country table, create
				// the country in this table ("billing_trading_country").
				tradingCountry = new TradingCountry();
				tradingCountry.setCountry(country);
				tradingCountry.setProvider(provider);
				tradingCountry.setActive(true);
				tradingCountry.setPrDescription(countryDto.getName());
				tradingCountry.setAuditable(auditableTrading);
				tradingCountryService.create(em, tradingCountry, currentUser,
						provider);

				// If currencyCode exist in reference table ("adm_currency") and
				// don't exist in the trading currency table, create the
				// currency in the trading currency table
				// ('billing_trading_currency").
				if (!StringUtils.isBlank(countryDto.getCurrencyCode())
						&& tradingCurrencyService.findByTradingCurrencyCode(
								countryDto.getCurrencyCode(), provider) != null) {
					TradingCurrency tradingCurrency = new TradingCurrency();
					tradingCurrency.setActive(true);
					tradingCurrency.setCurrency(currency);
					tradingCurrency.setAuditable(auditableTrading);
					tradingCurrency.setCurrencyCode(countryDto
							.getCurrencyCode());
					tradingCurrencyService.create(em, tradingCurrency,
							currentUser, provider);
				}
			} else {
				throw new EnvironmentException("Trading country code="
						+ tradingCountry.getCountryCode() + " already exists.");
			}

		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(countryDto.getCountryCode())) {
				missingFields.add("countryCode");
			}
			if (StringUtils.isBlank(countryDto.getName())) {
				missingFields.add("name");
			}
			if (StringUtils.isBlank(countryDto.getCurrencyCode())) {
				missingFields.add("currencyCode");
			}
			sb.append(org.apache.commons.lang.StringUtils.join(
					missingFields.toArray(), ", "));
			sb.append(".");

			throw new EnvironmentException(sb.toString());
		}
	}

	public CountryDto find(String countryCode) throws EnvironmentException {
		if (!StringUtils.isBlank(countryCode)) {
			Country country = countryService.findByCode(countryCode);
			if (country != null) {
				return new CountryDto(country);
			}

			throw new EnvironmentException("Country code " + countryCode
					+ " does not exists.");
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(countryCode)) {
				missingFields.add("countryCode");
			}
			sb.append(org.apache.commons.lang.StringUtils.join(
					missingFields.toArray(), ", "));
			sb.append(".");

			throw new EnvironmentException(sb.toString());
		}
	}

	public void remove(String countryCode, String currencyCode, Long providerId)
			throws EnvironmentException {
		Provider provider = providerService.findById(providerId);
		
		TradingCountry tradingCountry = tradingCountryService
				.findByTradingCountryCode(countryCode, provider);
		Currency currency = currencyService.findByCode(currencyCode);
		if (tradingCountry != null && currency != null) {
			if (tradingCountry != null) {
				tradingCountryService.remove(tradingCountry);
			}
		} else {
			if (tradingCountry == null) {
				throw new EnvironmentException("Trading Country code=" + countryCode
						+ " does not exists.");
			} else {
				throw new EnvironmentException("Currency code=" + currencyCode
						+ " does not exists.");
			}
		}
	}

	public void update(CountryDto countryDto) throws EnvironmentException {
		Country country = countryService
				.findByCode(countryDto.getCountryCode());
		if (country != null) {
			country.setDescriptionEn(countryDto.getName());
			Currency currency = currencyService.findByCode(countryDto
					.getCurrencyCode());
			if (currency != null) {
				country.setCurrency(currency);
			} else {
				throw new EnvironmentException("Currency code does not exists.");
			}
			if (countryDto.getLanguageCode() != null) {
				Language language = languageService.findByCode(countryDto
						.getLanguageCode());
				if (language != null) {
					country.setLanguage(language);
				} else {
					throw new EnvironmentException(
							"Language code does not exists.");
				}
			}
			countryService.update(country);
		} else {
			throw new EnvironmentException("Country code does not exists.");
		}
	}

}
