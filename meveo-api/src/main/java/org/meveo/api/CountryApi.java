package org.meveo.api;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.CountryDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
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
import org.meveo.service.billing.impl.TradingCountryService;

/**
 * @author Edward P. Legaspi
 * @since Oct 4, 2013
 **/
@Stateless
public class CountryApi extends BaseApi {

	@Inject
	private CountryService countryService;

	@Inject
	private TradingCountryService tradingCountryService;

	@Inject
	private CurrencyService currencyService;

	@Inject
	private TradingCurrencyService tradingCurrencyService;

	@Inject
	private LanguageService languageService;

	public void create(CountryDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCountryCode()) && !StringUtils.isBlank(postData.getName())
				&& !StringUtils.isBlank(postData.getCurrencyCode())) {

			// If countryCode exist in the trading country table
			// ("billing_trading_country"), return error.

			Provider provider = currentUser.getProvider();
			TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountryCode(),
					provider);

			if (tradingCountry == null) {
				// check currency
				Country country = countryService.findByCode(postData.getCountryCode());
				Currency currency = null;
				Auditable auditable = new Auditable();

				if (!StringUtils.isBlank(postData.getLanguageCode())) {
					Language language = languageService.findByCode(postData.getLanguageCode());
					if (language == null) {
						throw new EntityDoesNotExistsException(Language.class, postData.getLanguageCode());
					}

					country.setLanguage(language);
				}

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
					country.setDescriptionEn(postData.getName());
					country.setCountryCode(postData.getCountryCode());
				} else {
					auditable = country.getAuditable();
					auditable.setUpdated(new Date());
					auditable.setUpdater(currentUser);
				}
				country.setAuditable(auditable);

				if (postData.getCurrencyCode() != null) { //
					currency = currencyService.findByCode(postData.getCurrencyCode());
					// If currencyCode don't exist in reference table
					// ("adm_currency"), return error.
					if (currency == null) {
						throw new EntityDoesNotExistsException(Currency.class, postData.getCurrencyCode());
					}
				} else {
					if (provider.getCurrency() != null) {
						currency = provider.getCurrency();
					}
				}
				country.setCurrency(currency);

				if (country.isTransient()) {
					countryService.create(country, currentUser, provider);
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
				tradingCountry.setPrDescription(postData.getName());
				tradingCountry.setAuditable(auditableTrading);
				tradingCountryService.create(tradingCountry, currentUser, provider);

				// If currencyCode exist in reference table ("adm_currency") and
				// don't exist in the trading currency table, create the
				// currency in the trading currency table
				// ('billing_trading_currency").
				if (!StringUtils.isBlank(postData.getCurrencyCode())
						&& tradingCurrencyService.findByTradingCurrencyCode(postData.getCurrencyCode(), provider) == null) {
					TradingCurrency tradingCurrency = new TradingCurrency();
					tradingCurrency.setActive(true);
					tradingCurrency.setCurrency(currency);
					tradingCurrency.setAuditable(auditableTrading);
					tradingCurrency.setCurrencyCode(postData.getCurrencyCode());
					tradingCurrency.setPrDescription(postData.getCurrencyCode());
					tradingCurrencyService.create(tradingCurrency, currentUser, provider);
				}
			} else {
				throw new EntityAlreadyExistsException(TradingCountry.class.getName(), tradingCountry.getCountryCode());
			}
		} else {
			if (StringUtils.isBlank(postData.getCountryCode())) {
				missingParameters.add("countryCode");
			}
			if (StringUtils.isBlank(postData.getName())) {
				missingParameters.add("name");
			}
			if (StringUtils.isBlank(postData.getCurrencyCode())) {
				missingParameters.add("currencyCode");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public CountryDto find(String countryCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(countryCode)) {
			TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(countryCode, provider);

			if (tradingCountry != null) {
				Country country = countryService.findByCode(countryCode);
				return new CountryDto(tradingCountry, country);
			}

			throw new EntityDoesNotExistsException(TradingCountry.class, countryCode);
		} else {
			if (StringUtils.isBlank(countryCode)) {
				missingParameters.add("countryCode");
			}

			throw new MeveoApiException(getMissingParametersExceptionMessage());
		}
	}

	public void remove(String countryCode, String currencyCode, Provider provider) throws MeveoApiException {

		if (!StringUtils.isBlank(countryCode) && !StringUtils.isBlank(currencyCode)) {
			TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(countryCode, provider);
			Currency currency = currencyService.findByCode(currencyCode);
			if (tradingCountry != null && currency != null) {
				if (tradingCountry != null) {
					tradingCountryService.remove(tradingCountry);
				}
			} else {
				if (tradingCountry == null) {
					throw new EntityDoesNotExistsException(TradingCountry.class, countryCode);
				} else {
					throw new EntityDoesNotExistsException(Currency.class, currencyCode);
				}
			}
		} else {
			if (StringUtils.isBlank(countryCode)) {
				missingParameters.add("countryCode");
			}
			if (StringUtils.isBlank(currencyCode)) {
				missingParameters.add("currencyCode");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void update(CountryDto postData, User currentUser) throws MeveoApiException {
		Provider provider = currentUser.getProvider();
		if (!StringUtils.isBlank(postData.getCountryCode()) && !StringUtils.isBlank(postData.getCurrencyCode())) {
			Currency currency = currencyService.findByCode(postData.getCurrencyCode());
			TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountryCode(),
					provider);

			Language language = languageService.findByCode(postData.getLanguageCode());
			if (language == null) {
				throw new EntityDoesNotExistsException(Language.class, postData.getLanguageCode());
			}

			if (currency != null && tradingCountry != null) {
				Country country = countryService.findByCode(postData.getCountryCode());
				if (country != null && !StringUtils.isBlank(postData.getName())) {
					if (!country.getDescriptionEn().equals(postData.getName())) {
						tradingCountry.setPrDescription(postData.getName());
						country.setCurrency(currency);
						country.setDescriptionEn(postData.getName());
						country.setLanguage(language);
					}

					TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(
							postData.getCurrencyCode(), provider);
					if (tradingCurrency == null) {
						Auditable auditableTrading = new Auditable();
						auditableTrading.setCreated(new Date());
						auditableTrading.setCreator(currentUser);

						tradingCurrency = new TradingCurrency();
						tradingCurrency.setActive(true);
						tradingCurrency.setCurrency(currency);
						tradingCurrency.setAuditable(auditableTrading);
						tradingCurrency.setCurrencyCode(postData.getCurrencyCode());
						tradingCurrency.setPrDescription(postData.getCurrencyCode());
						tradingCurrencyService.create(tradingCurrency, currentUser, provider);
					}
				} else {
					throw new EntityDoesNotExistsException(Country.class, postData.getCountryCode());
				}

			} else {
				if (currency == null) {
					throw new EntityDoesNotExistsException(Currency.class, postData.getCurrencyCode());
				}
				if (tradingCountry == null) {
					throw new EntityDoesNotExistsException(TradingCountry.class, postData.getCountryCode());
				}
			}
		} else {
			if (StringUtils.isBlank(postData.getCountryCode())) {
				missingParameters.add("countryCode");
			}
			if (StringUtils.isBlank(postData.getCurrencyCode())) {
				missingParameters.add("currencyCode");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void createOrUpdate(CountryDto postData, User currentUser) throws MeveoApiException {
		TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountryCode(),
				currentUser.getProvider());
		if (tradingCountry == null) {
			// create
			create(postData, currentUser);
		} else {
			// update
			update(postData, currentUser);
		}
	}
}
