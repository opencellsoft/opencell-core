/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.CountryDto;
import org.meveo.api.dto.TradingCountriesDto;
import org.meveo.api.dto.TradingCountryDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.generic.GenericPagingAndFilteringUtils;
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

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

/**
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 8.0.0
 * @since Oct 4, 2013
 */
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

    public TradingCountriesDto list() {
        TradingCountriesDto result = new TradingCountriesDto();

        List<TradingCountry> countries =
                tradingCountryService.list(GenericPagingAndFilteringUtils.getInstance().getPaginationConfiguration());
        if (countries != null) {
            for (TradingCountry country : countries) {
                result.getCountry().add(new TradingCountryDto(country));
            }
        }

        return result;
    }

    public void create(CountryDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCountryCode())) {
            String generatedCode = getGenericCode(Country.class.getName());
            if (generatedCode != null) {
                postData.setCountryCode(generatedCode);
            } else {
                missingParameters.add("countryCode");
            }
        }

        handleMissingParameters();

        // If countryCode exist in the trading country table ("billing_trading_country"), return error.

        TradingCountry tradingCountry = tradingCountryService.findByCode(postData.getCountryCode());
        if (tradingCountry != null) {
            throw new EntityAlreadyExistsException(TradingCountry.class.getName(), tradingCountry.getCountryCode());
        }

        Country country = countryService.findByCode(postData.getCountryCode());

        // If country code doesn't exist in the reference table, create the country in this table ("adm_country") with the currency code for the default provider.
        if (country == null) {
            country = new Country();
            country.setDescription(postData.getName());
            country.setCountryCode(postData.getCountryCode());
            if(postData.getLanguageDescriptions() != null) {
                country.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), null));
            }
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
        tradingCountry.setDescription(postData.getName());
        if (postData.isDisabled() != null) {
            tradingCountry.setDisabled(postData.isDisabled());
        }

        tradingCountryService.create(tradingCountry);

        // If currencyCode exist in reference table ("adm_currency") and don't exist in the trading currency table, create the currency in the trading currency table
        // ('billing_trading_currency").
        if (currency != null && tradingCurrencyService.findByTradingCurrencyCode(currency.getCurrencyCode()) == null) {
            TradingCurrency tradingCurrency = new TradingCurrency();
            tradingCurrency.setActive(true);
            tradingCurrency.setCurrency(currency);
            tradingCurrency.setCurrencyCode(postData.getCurrencyCode());
            tradingCurrency.setPrDescription(postData.getCurrencyCode());
            if (postData.isDisabled() != null) {
                tradingCurrency.setDisabled(postData.isDisabled());
            }
            tradingCurrencyService.create(tradingCurrency);
        }
    }

    public CountryDto find(String countryCode) throws MeveoApiException {
        if (StringUtils.isBlank(countryCode)) {
            missingParameters.add("countryCode");
        }

        handleMissingParameters();

        TradingCountry tradingCountry = tradingCountryService.findByCode(countryCode);

        if (tradingCountry == null) {
            throw new EntityDoesNotExistsException(TradingCountry.class, countryCode);
        }
        Country country = countryService.findByCode(countryCode);
        return new CountryDto(tradingCountry, country);
    }

    public void remove(String countryCode, String currencyCode) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(countryCode)) {
            missingParameters.add("countryCode");
        }

        handleMissingParameters();

        TradingCountry tradingCountry = tradingCountryService.findByCode(countryCode);
        if (tradingCountry == null) {
            throw new EntityDoesNotExistsException(TradingCountry.class, countryCode);
        }
        tradingCountryService.remove(tradingCountry);
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
        TradingCountry tradingCountry = tradingCountryService.findByCode(postData.getCountryCode());
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

        if (!StringUtils.isBlank(postData.getName()) && (!postData.getName().equals(country.getDescription()) || !postData.getName().equals(tradingCountry.getDescription()))) {
            tradingCountry.setDescription(postData.getName());
            country.setCurrency(currency);
            country.setDescription(postData.getName());
            if(postData.getLanguageDescriptions() != null) {
                country.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), null));
            }
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
        if (!StringUtils.isBlank(postData.getCountryCode())
                && tradingCountryService.findByCode(postData.getCountryCode()) != null) {
            update(postData);
        } else {
            create(postData);
        }
    }

    public void findOrCreate(String countryCode) throws EntityDoesNotExistsException, BusinessException {
        if (StringUtils.isBlank(countryCode)) {
            return;
        }
        TradingCountry tradingCountry = tradingCountryService.findByCode(countryCode);
        if (tradingCountry == null) {
            Country country = countryService.findByCode(countryCode);
            if (country == null) {
                throw new EntityDoesNotExistsException(Country.class, countryCode);
            }
            tradingCountry = new TradingCountry();
            tradingCountry.setCountry(country);
            tradingCountry.setDescription(country.getDescription());
            tradingCountryService.create(tradingCountry);
        }
    }

    /**
     * Enable or disable Trading country
     * 
     * @param code Country code
     * @param enable Should Trading country be enabled
     * @throws EntityDoesNotExistsException Entity does not exist
     * @throws MissingParameterException Missing parameters
     * @throws BusinessException A general business exception
     */
    public void enableOrDisable(String code, boolean enable) throws EntityDoesNotExistsException, MissingParameterException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        TradingCountry tradingCountry = tradingCountryService.findByCode(code);
        if (tradingCountry == null) {
            throw new EntityDoesNotExistsException(TradingCountry.class, code);
        }
        if (enable) {
            tradingCountryService.enable(tradingCountry);
        } else {
            tradingCountryService.disable(tradingCountry);
        }
    }
}