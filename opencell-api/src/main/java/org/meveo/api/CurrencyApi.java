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
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CurrenciesDto;
import org.meveo.api.dto.CurrencyDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.rest.exception.NotFoundException;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.crm.impl.ProviderService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

/**
 * @author Edward P. Legaspi
 * 
 **/
@Stateless
public class CurrencyApi extends BaseApi {

    @Inject
    private CurrencyService currencyService;

    @Inject
    private TradingCurrencyService tradingCurrencyService;

    @Inject
    private ProviderService providerService;

    public CurrenciesDto list() {
        CurrenciesDto result = new CurrenciesDto();

        List<TradingCurrency> currencies =
                tradingCurrencyService.list(GenericPagingAndFilteringUtils.getInstance().getPaginationConfiguration());
        if (currencies != null) {
            for (TradingCurrency country : currencies) {
                result.getCurrency().add(new CurrencyDto(country));
            }
        }

        return result;
    }

    public CurrencyDto create(CurrencyDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            String generatedCode = getGenericCode(Currency.class.getName());
            if (generatedCode != null) {
                postData.setCode(generatedCode);
            } else {
                missingParameters.add("code");
            }
        }

        handleMissingParameters();

        if (tradingCurrencyService.findByTradingCurrencyCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(TradingCurrency.class, postData.getCode());
        }

        Currency currency = currencyService.findByCode(postData.getCode());

        if (currency == null) {
            // create
            currency = new Currency();
            currency.setCurrencyCode(postData.getCode());
            currency.setDescriptionEn(postData.getDescription());
            currency.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), null));
            currencyService.create(currency);
        }

        TradingCurrency tradingCurrency = new TradingCurrency();
        tradingCurrency.setCurrency(currency);
        tradingCurrency.setCurrencyCode(postData.getCode());
        tradingCurrency.setPrDescription(postData.getDescription());
        tradingCurrency.setActive(true);
        tradingCurrency.setPrCurrencyToThis(postData.getPrCurrencyToThis());
        tradingCurrency.setSymbol(postData.getSymbol() != null ? postData.getSymbol() : postData.getCode());
        tradingCurrency.setDecimalPlaces(postData.getDecimalPlaces());
        if (postData.isDisabled() != null) {
            tradingCurrency.setDisabled(postData.isDisabled());
        }
        tradingCurrencyService.create(tradingCurrency);
        return new CurrencyDto(currency);
    }

    public CurrencyDto find(String code) throws MissingParameterException, EntityDoesNotExistsException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }
        handleMissingParameters();

        TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(code);

        if (tradingCurrency == null) {
            throw new EntityDoesNotExistsException(TradingLanguage.class, code);
        }
        return new CurrencyDto(tradingCurrency);
    }

    public void remove(String code) throws BusinessException, MissingParameterException, EntityDoesNotExistsException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }
        handleMissingParameters();

        TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(code);
        if (tradingCurrency == null) {
            throw new EntityDoesNotExistsException(TradingCurrency.class, code);
        }

        tradingCurrencyService.remove(tradingCurrency);
    }

    public void update(CurrencyDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        handleMissingParameters();

        TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(postData.getCode());
        if (tradingCurrency == null) {
            throw new EntityDoesNotExistsException(TradingCurrency.class, postData.getCode());
        }

        Currency currency = currencyService.findByCode(postData.getCode());

        if (currency == null) {
            throw new EntityDoesNotExistsException(Currency.class, postData.getCode());
        }
        currency.setDescriptionEn(postData.getDescription());
        currency.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), null));
        currency = currencyService.update(currency);

        tradingCurrency.setCurrency(currency);
        tradingCurrency.setPrDescription(postData.getDescription());
        tradingCurrency.setPrCurrencyToThis(postData.getPrCurrencyToThis());
        tradingCurrency.setDecimalPlaces(postData.getDecimalPlaces() == null ? 2 : postData.getDecimalPlaces());

        tradingCurrencyService.update(tradingCurrency);
    }

    public void createOrUpdate(CurrencyDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())
                && tradingCurrencyService.findByTradingCurrencyCode(postData.getCode()) != null) {
            update(postData);
        } else {
            create(postData);
        }
    }

    public void findOrCreate(String currencyCode) throws EntityDoesNotExistsException, BusinessException {
        if (StringUtils.isBlank(currencyCode)) {
            return;
        }
        TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(currencyCode);
        if (tradingCurrency == null) {
            Currency currency = currencyService.findByCode(currencyCode);
            if (currency == null) {
                throw new EntityDoesNotExistsException(Currency.class, currencyCode);
            }
            tradingCurrency = new TradingCurrency();
            tradingCurrency.setCurrency(currency);
            tradingCurrency.setPrDescription(currency.getDescriptionEn());
            tradingCurrencyService.create(tradingCurrency);
        }
    }

    /**
     * Enable or disable Trading currency
     * 
     * @param code Currency code
     * @param enable Should Trading currency be enabled
     * @throws EntityDoesNotExistsException Entity does not exist
     * @throws MissingParameterException Missing parameters
     * @throws BusinessException A general business exception
     */
    public void enableOrDisable(String code, boolean enable) throws EntityDoesNotExistsException, MissingParameterException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(code);
        if (tradingCurrency == null) {
            throw new EntityDoesNotExistsException(TradingCurrency.class, code);
        }
        if (enable) {
            tradingCurrencyService.enable(tradingCurrency);
        } else {
            tradingCurrencyService.disable(tradingCurrency);
        }
    }

    public ActionStatus addFunctionalCurrency(CurrencyDto postData) {
        if(postData.getCode()== null)
        {
            throw new MissingParameterException("code of the currency is mandatory");
        }
        Currency currency = currencyService.findByCode(postData.getCode());
        if(currency == null)
        {
            throw new NotFoundException(new ActionStatus(ActionStatusEnum.FAIL, "currency not found"));
        }

        Provider provider = providerService.findById(appProvider.getId());
        provider.setCurrency(currency);
        provider.setMulticurrencyFlag(true);
        provider.setFunctionalCurrencyFlag(true);
        providerService.update(provider);


        return new ActionStatus(ActionStatusEnum.SUCCESS, "Success");
    }
}