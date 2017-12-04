package org.meveo.api;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.CountryDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.Country;
import org.meveo.model.billing.Language;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.LanguageService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.TradingCountryService;

/**
 * @author Edward P. Legaspi
 * @since Oct 4, 2013
 * 
 * @deprecated will be renammed to TradingCountryApi
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

    public void create(CountryDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCountryCode())) {
            missingParameters.add("countryCode");
        }

        handleMissingParameters();
        

        // If countryCode exist in the trading country table ("billing_trading_country"), return error.
        
        TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountryCode());
        if (tradingCountry != null) {
            throw new EntityAlreadyExistsException(TradingCountry.class.getName(), tradingCountry.getCountryCode());
        }

        Country country = countryService.findByCode(postData.getCountryCode());

        // If country code doesn't exist in the reference table, create the country in this table ("adm_country") with the currency code for the default provider.
        if (country == null) {
            country = new Country();
            country.setDescription(postData.getName());
            country.setCountryCode(postData.getCountryCode());
        }
        if (!StringUtils.isBlank(postData.getLanguageCode())) {
            Language language = languageService.findByCode(postData.getLanguageCode());
            if (language == null) {
                throw new EntityDoesNotExistsException(Language.class, postData.getLanguageCode());
            }

            country.setLanguage(language);
        }

        Currency currency = null;
        if (postData.getCurrencyCode() != null) { //
            currency = currencyService.findByCode(postData.getCurrencyCode());
            // If currencyCode don't exist in reference table ("adm_currency"), return error.
            if (currency == null) {
                throw new EntityDoesNotExistsException(Currency.class, postData.getCurrencyCode());
            }
            country.setCurrency(currency);

        } else {
            if (appProvider.getCurrency() != null) {
                currency = appProvider.getCurrency();
                country.setCurrency(currency);
            }
        }
        if (country.isTransient()) {
            countryService.create(country);
        } else {
            countryService.update(country);
        }

        // If country don't exist in the trading country table, create the country in this table ("billing_trading_country").
        tradingCountry = new TradingCountry();
        tradingCountry.setCountry(country);
        tradingCountry.setActive(true);
        tradingCountry.setPrDescription(postData.getName());
        tradingCountryService.create(tradingCountry);

        // If currencyCode exist in reference table ("adm_currency") and don't exist in the trading currency table, create the currency in the trading currency table
        // ('billing_trading_currency").
        if (currency != null && tradingCurrencyService.findByTradingCurrencyCode(currency.getCurrencyCode()) == null) {
            TradingCurrency tradingCurrency = new TradingCurrency();
            tradingCurrency.setActive(true);
            tradingCurrency.setCurrency(currency);
            tradingCurrency.setCurrencyCode(postData.getCurrencyCode());
            tradingCurrency.setPrDescription(postData.getCurrencyCode());
            tradingCurrencyService.create(tradingCurrency);
        }
    }

    public CountryDto find(String countryCode) throws MeveoApiException {
        if (StringUtils.isBlank(countryCode)) {
            missingParameters.add("countryCode");
        }
        
        handleMissingParameters();

        TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(countryCode);

        if (tradingCountry != null) {
            Country country = countryService.findByCode(countryCode);
            return new CountryDto(tradingCountry, country);
        }

        throw new EntityDoesNotExistsException(TradingCountry.class, countryCode);
    }

    public void remove(String countryCode, String currencyCode) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(countryCode)) {
            missingParameters.add("countryCode");
        }

        handleMissingParameters();
        

        TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(countryCode);
        if (tradingCountry != null) {
            if (tradingCountry != null) {
                tradingCountryService.remove(tradingCountry);
            }
        } else {
        	throw new EntityDoesNotExistsException(TradingCountry.class, countryCode);
        }
    }

    public void update(CountryDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCountryCode())) {
            missingParameters.add("countryCode");
        }
        if (StringUtils.isBlank(postData.getCurrencyCode())) {
            missingParameters.add("currencyCode");
        }

        handleMissingParameters();
        

        
        Currency currency = currencyService.findByCode(postData.getCurrencyCode());
        if (currency == null) {
            throw new EntityDoesNotExistsException(Currency.class, postData.getCurrencyCode());
        }
        TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountryCode());
        if (tradingCountry == null) {
            throw new EntityDoesNotExistsException(TradingCountry.class, postData.getCountryCode());
        }
        Country country = countryService.findByCode(postData.getCountryCode());
        if (country == null) {
            throw new EntityDoesNotExistsException(Country.class, postData.getCountryCode());
        }

        Language language = null;
        if (!StringUtils.isBlank(postData.getLanguageCode())) {
            language = languageService.findByCode(postData.getLanguageCode());
            if (language == null) {
                throw new EntityDoesNotExistsException(Language.class, postData.getLanguageCode());
            }
        }

        if (!StringUtils.isBlank(postData.getName()) && (!postData.getName().equals(country.getDescription()) || !postData.getName().equals(tradingCountry.getPrDescription()))) {
            tradingCountry.setPrDescription(postData.getName());
            country.setCurrency(currency);
            country.setDescription(postData.getName());

            if (language != null) {
                country.setLanguage(language);
            }
        }

        TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(postData.getCurrencyCode());
        if (tradingCurrency == null) {
            tradingCurrency = new TradingCurrency();
            tradingCurrency.setActive(true);
            tradingCurrency.setCurrency(currency);
            tradingCurrency.setCurrencyCode(postData.getCurrencyCode());
            tradingCurrency.setPrDescription(postData.getCurrencyCode());
            tradingCurrencyService.create(tradingCurrency);
        }
    }

    public void createOrUpdate(CountryDto postData) throws MeveoApiException, BusinessException {
        TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountryCode());
        if (tradingCountry == null) {
            // create
            create(postData);
        } else {
            // update
            update(postData);
        }
    }
    public void findOrCreate(String countryCode) throws EntityDoesNotExistsException, BusinessException {
        if (StringUtils.isBlank(countryCode)){
            return;
        }
		TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(countryCode);
		if (tradingCountry==null) {
			Country country = countryService.findByCode(countryCode);
			if (country==null) {
				throw new EntityDoesNotExistsException(Country.class, countryCode);
			}
			tradingCountry = new TradingCountry();
			tradingCountry.setCountry(country);
			tradingCountry.setPrDescription(country.getDescription());
			tradingCountryService.create(tradingCountry);
		}
	}
}