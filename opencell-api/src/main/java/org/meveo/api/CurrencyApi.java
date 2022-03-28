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

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CurrenciesDto;
import org.meveo.api.dto.CurrencyDto;
import org.meveo.api.dto.billing.ExchangeRateDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.rest.exception.NotFoundException;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.ExchangeRate;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.Provider;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.billing.impl.ExchangeRateService;
import org.meveo.service.crm.impl.ProviderService;

import static org.meveo.service.admin.impl.TradingCurrencyService.getCurrencySymbol;

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

    @Inject
    private ExchangeRateService exchangeRateService;

    @Inject
    private ResourceBundle resourceMessages;
    
    @Inject
    private AuditLogService auditLogService;

    public CurrenciesDto list() {
        CurrenciesDto result = new CurrenciesDto();

        List<TradingCurrency> currencies = tradingCurrencyService.list(GenericPagingAndFilteringUtils.getInstance().getPaginationConfiguration());
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
        tradingCurrency.setSymbol(getCurrencySymbol(postData.getCode()));
        tradingCurrency.setDecimalPlaces(postData.getDecimalPlaces());
        if (postData.isDisabled() != null) {
            tradingCurrency.setDisabled(postData.isDisabled());
        }
        tradingCurrencyService.create(tradingCurrency);
        return new CurrencyDto(tradingCurrency);
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
        tradingCurrency.setSymbol(getCurrencySymbol(postData.getCode()));
        tradingCurrency.setDecimalPlaces(postData.getDecimalPlaces() == null ? 2 : postData.getDecimalPlaces());

        tradingCurrencyService.update(tradingCurrency);
    }

    public void createOrUpdate(CurrencyDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode()) && tradingCurrencyService.findByTradingCurrencyCode(postData.getCode()) != null) {
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
        List<ExchangeRate> listExchangeRate = tradingCurrency.getExchangeRates();
        if (enable) {
            tradingCurrencyService.enable(tradingCurrency);
            for (ExchangeRate oneExchangeRate : listExchangeRate) {
                exchangeRateService.enable(oneExchangeRate);
            }
        } else {
            tradingCurrencyService.disable(tradingCurrency);
            for (ExchangeRate oneExchangeRate : listExchangeRate) {
                exchangeRateService.disable(oneExchangeRate);
            }
        }
    }

    public ActionStatus addFunctionalCurrency(CurrencyDto postData) {
        if (postData.getCode() == null) {
            throw new MissingParameterException("code of the currency is mandatory");
        }
        Currency currency = currencyService.findByCode(postData.getCode());
        if (currency == null) {
            throw new NotFoundException(new ActionStatus(ActionStatusEnum.FAIL, "currency not found"));
        }

        Provider provider = providerService.getProviderNoCache();
        provider.setCurrency(currency);
        provider.setMulticurrencyFlag(true);
        provider.setFunctionalCurrencyFlag(true);
        providerService.update(provider);
        TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(currency.getCurrencyCode());
        if(tradingCurrency == null)
        {
            tradingCurrency = new TradingCurrency();
            tradingCurrency.setCurrencyCode(currency.getCurrencyCode());
            tradingCurrency.setPrDescription(currency.getDescription());
            tradingCurrency.setSymbol(getCurrencySymbol(postData.getCode()));
            tradingCurrency.setDecimalPlaces(2);
            tradingCurrencyService.create(tradingCurrency);
        }

        return new ActionStatus(ActionStatusEnum.SUCCESS, "Success");
    }

    public Long addExchangeRate(org.meveo.api.dto.ExchangeRateDto postData) throws MeveoApiException {
        if (postData.getTradingCurrency() == null) {
            throw new MeveoApiException(resourceMessages.getString("error.exchangeRate.tradingCurrency.mandatory"));
        }        
        
        TradingCurrency tradingCurrency = tradingCurrencyService.findById(postData.getTradingCurrency().getId());        
        if (tradingCurrency == null) {
            throw new MeveoApiException(resourceMessages.getString("error.exchangeRate.valide.tradingCurrency"));
        }
        
        ExchangeRate exchangeRate = exchangeRateService.createCurrentRateWithPostData(postData, tradingCurrency);              
        return exchangeRate.getId();
    }

    public void updateExchangeRate(Long id, ExchangeRateDto postData) {

        ExchangeRate exchangeRate = exchangeRateService.findById(id);
        if (exchangeRate == null) {
            throw new EntityDoesNotExistsException(ExchangeRate.class, id);
        }
        
        BigDecimal fromRate = exchangeRate.getExchangeRate();
        BigDecimal toRate = postData.getExchangeRate();
        
        Date fromDate = exchangeRate.getFromDate();
        Date toDate = postData.getFromDate();
        
        // We can modify only the future rates
        if (exchangeRate.getFromDate().compareTo(DateUtils.setTimeToZero(new Date())) <= 0) {
            throw new BusinessApiException(resourceMessages.getString("error.exchangeRate.fromDate.future"));
        }

        if (postData.getFromDate() == null) {
            throw new MissingParameterException(resourceMessages.getString("error.exchangeRate.fromDate.empty"));
        }

        // Check if a user choose a date that is already taken for the same TradingCurrency
        TradingCurrency tradingCurrency = tradingCurrencyService.findById(exchangeRate.getTradingCurrency().getId(), Arrays.asList("exchangeRates"));
        for (ExchangeRate er : tradingCurrency.getExchangeRates()) {
            if (!er.getId().equals(id) && er.getFromDate().compareTo(postData.getFromDate()) == 0) {
                throw new BusinessApiException(resourceMessages.getString("error.exchangeRate.fromDate.isAlreadyTaken"));
            }
        }

        // User cannot set a rate in a paste date
        if (postData.getFromDate().before(DateUtils.setTimeToZero(new Date()))) {
            throw new BusinessApiException(resourceMessages.getString("error.exchangeRate.fromDate.past"));
        }

        // Check if fromDate = new Date()
        if (postData.getFromDate().compareTo(DateUtils.setTimeToZero(new Date())) == 0) {
            exchangeRate.setCurrentRate(true);
            // set isCurrentRate to false for all other ExchangeRate of the same TradingCurrency
            for (ExchangeRate er : tradingCurrency.getExchangeRates()) {
                if (!er.getId().equals(id)) {
                    er.setCurrentRate(false);
                    exchangeRateService.update(er);
                }
            }

            // Update tradingCurrency fields
            tradingCurrency.setCurrentRate(postData.getExchangeRate());
            tradingCurrency.setCurrentRateFromDate(postData.getFromDate());
            tradingCurrency.setCurrentRateUpdater(currentUser.getUserName());
            tradingCurrencyService.update(tradingCurrency);
        }
        exchangeRate.setFromDate(postData.getFromDate());
        exchangeRate.setExchangeRate(postData.getExchangeRate());
        exchangeRateService.update(exchangeRate);
        auditLogUpdateExchangeRate(exchangeRate, fromDate, toDate, fromRate, toRate);
    }
    
    private void auditLogUpdateExchangeRate(ExchangeRate exchangeRate, 
            Date fromDate, Date toDate,
            BigDecimal fromRateAmount, BigDecimal toRateAmount) {

        DecimalFormat rateFormatter = new DecimalFormat("#0.##");
        DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

        String parameters = "User " + auditLogService.getActor() + " has changed ";
        boolean addAnd = false;
        if (!fromRateAmount.equals(toRateAmount)) {
            parameters += "for " + exchangeRate.getTradingCurrency().getCurrencyCode() + " from " + rateFormatter.format(fromRateAmount) + " to " + rateFormatter.format(toRateAmount);
            addAnd = true;
        }
        if (!fromDate.equals(toDate)) {
            if (addAnd) {
                parameters += " AND ";
            }
            parameters += "From " + dateFormatter.format(fromDate) + " to " + dateFormatter.format(toRateAmount);
        }
        auditLogService.trackOperation("UPDATE", new Date(), exchangeRate, "API", parameters);
    }
}