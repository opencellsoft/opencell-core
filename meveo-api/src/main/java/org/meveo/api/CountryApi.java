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

    public void create(CountryDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCountryCode())) {
            missingParameters.add("countryCode");
        }

        handleMissingParameters();
        

        // If countryCode exist in the trading country table ("billing_trading_country"), return error.
        Provider provider = currentUser.getProvider();
        TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountryCode(), provider);
        if (tradingCountry != null) {
            throw new EntityAlreadyExistsException(TradingCountry.class.getName(), tradingCountry.getCountryCode());
        }

        Country country = countryService.findByCode(postData.getCountryCode());

        // If country code doesn't exist in the reference table, create the country in this table ("adm_country") with the currency code for the default provider.
        if (country == null) {
            country = new Country();
            country.setDescriptionEn(postData.getName());
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
            if (provider.getCurrency() != null) {
                currency = provider.getCurrency();
                country.setCurrency(currency);
            }
        }
        if (country.isTransient()) {
            countryService.create(country, currentUser);
        } else {
            countryService.update(country, currentUser);
        }

        // If country don't exist in the trading country table, create the country in this table ("billing_trading_country").
        tradingCountry = new TradingCountry();
        tradingCountry.setCountry(country);
        tradingCountry.setProvider(provider);
        tradingCountry.setActive(true);
        tradingCountry.setPrDescription(postData.getName());
        tradingCountryService.create(tradingCountry, currentUser);

        // If currencyCode exist in reference table ("adm_currency") and don't exist in the trading currency table, create the currency in the trading currency table
        // ('billing_trading_currency").
        if (currency != null && tradingCurrencyService.findByTradingCurrencyCode(currency.getCurrencyCode(), provider) == null) {
            TradingCurrency tradingCurrency = new TradingCurrency();
            tradingCurrency.setActive(true);
            tradingCurrency.setCurrency(currency);
            tradingCurrency.setCurrencyCode(postData.getCurrencyCode());
            tradingCurrency.setPrDescription(postData.getCurrencyCode());
            tradingCurrencyService.create(tradingCurrency, currentUser);
        }
    }

    public CountryDto find(String countryCode, Provider provider) throws MeveoApiException {
        if (StringUtils.isBlank(countryCode)) {
            missingParameters.add("countryCode");
        }
        
        handleMissingParameters();

        TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(countryCode, provider);

        if (tradingCountry != null) {
            Country country = countryService.findByCode(countryCode);
            return new CountryDto(tradingCountry, country);
        }

        throw new EntityDoesNotExistsException(TradingCountry.class, countryCode);
    }

    public void remove(String countryCode, String currencyCode, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(countryCode)) {
            missingParameters.add("countryCode");
        }

        handleMissingParameters();
        

        TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(countryCode, currentUser.getProvider());
        if (tradingCountry != null) {
            if (tradingCountry != null) {
                tradingCountryService.remove(tradingCountry, currentUser);
            }
        } else {
        	throw new EntityDoesNotExistsException(TradingCountry.class, countryCode);
        }
    }

    public void update(CountryDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCountryCode())) {
            missingParameters.add("countryCode");
        }
        if (StringUtils.isBlank(postData.getCurrencyCode())) {
            missingParameters.add("currencyCode");
        }

        handleMissingParameters();
        

        Provider provider = currentUser.getProvider();
        Currency currency = currencyService.findByCode(postData.getCurrencyCode());
        if (currency == null) {
            throw new EntityDoesNotExistsException(Currency.class, postData.getCurrencyCode());
        }
        TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountryCode(), provider);
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

        if (!StringUtils.isBlank(postData.getName()) && (!postData.getName().equals(country.getDescriptionEn()) || !postData.getName().equals(tradingCountry.getPrDescription()))) {
            tradingCountry.setPrDescription(postData.getName());
            country.setCurrency(currency);
            country.setDescriptionEn(postData.getName());

            if (language != null) {
                country.setLanguage(language);
            }
        }

        TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(postData.getCurrencyCode(), provider);
        if (tradingCurrency == null) {
            tradingCurrency = new TradingCurrency();
            tradingCurrency.setActive(true);
            tradingCurrency.setCurrency(currency);
            tradingCurrency.setCurrencyCode(postData.getCurrencyCode());
            tradingCurrency.setPrDescription(postData.getCurrencyCode());
            tradingCurrencyService.create(tradingCurrency, currentUser);
        }
    }

    public void createOrUpdate(CountryDto postData, User currentUser) throws MeveoApiException, BusinessException {
        TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountryCode(), currentUser.getProvider());
        if (tradingCountry == null) {
            // create
            create(postData, currentUser);
        } else {
            // update
            update(postData, currentUser);
        }
    }
    public void findOrCreate(String countryCode, User currentUser) throws EntityDoesNotExistsException, BusinessException {
        if (StringUtils.isBlank(countryCode)){
            return;
        }
		TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(countryCode, currentUser.getProvider());
		if (tradingCountry==null) {
			Country country = countryService.findByCode(countryCode);
			if (country==null) {
				throw new EntityDoesNotExistsException(Country.class, countryCode);
			}
			tradingCountry = new TradingCountry();
			tradingCountry.setCountry(country);
			tradingCountry.setPrDescription(country.getDescriptionEn());
			tradingCountryService.create(tradingCountry, currentUser);
		}
	}
}