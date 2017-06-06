package org.meveo.api;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.CurrencyDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.TradingCurrencyService;

/**
 * @author Edward P. Legaspi
 * 
 * @deprecated will be renammed to TradingCurrencyApi
 **/
@Stateless
public class CurrencyApi extends BaseApi {

    @Inject
    private CurrencyService currencyService;

    @Inject
    private TradingCurrencyService tradingCurrencyService;

    public void create(CurrencyDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
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
            currencyService.create(currency);
        }

        TradingCurrency tradingCurrency = new TradingCurrency();
        tradingCurrency.setCurrency(currency);
        tradingCurrency.setCurrencyCode(postData.getCode());
        tradingCurrency.setPrDescription(postData.getDescription());
        tradingCurrency.setActive(true);
        tradingCurrencyService.create(tradingCurrency);
    }

    public CurrencyDto find(String code) throws MissingParameterException, EntityDoesNotExistsException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }
        handleMissingParameters();

        TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(code);

        if (tradingCurrency != null) {
            return new CurrencyDto(tradingCurrency);
        }

        throw new EntityDoesNotExistsException(TradingLanguage.class, code);
    }

    public void remove(String code) throws BusinessException, MissingParameterException, EntityDoesNotExistsException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }
        handleMissingParameters();

        TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(code);
        if (tradingCurrency == null) {
            throw new EntityDoesNotExistsException(TradingCurrency.class, code);
        } else {
            tradingCurrencyService.remove(tradingCurrency);
        }
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

        if (currency != null) {

            currency.setDescriptionEn(postData.getDescription());
            currency = currencyService.update(currency);
            
            tradingCurrency.setCurrency(currency);
            tradingCurrency.setCurrencyCode(postData.getCode());
            tradingCurrency.setPrDescription(postData.getDescription());
            tradingCurrencyService.update(tradingCurrency);
            
        } else {
            throw new EntityDoesNotExistsException(Currency.class, postData.getCode());
        }
    }

    public void createOrUpdate(CurrencyDto postData) throws MeveoApiException, BusinessException {
        TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(postData.getCode());
        if (tradingCurrency == null) {
            create(postData);
        } else {
            update(postData);
        }
    }
    public void findOrCreate(String currencyCode) throws EntityDoesNotExistsException, BusinessException {
        if (StringUtils.isBlank(currencyCode)){
            return;
        }
		TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(currencyCode);
		if (tradingCurrency==null) {
			Currency currency = currencyService.findByCode(currencyCode);
			if (currency==null) {
				throw new EntityDoesNotExistsException(Currency.class, currencyCode);
			}
			tradingCurrency = new TradingCurrency();
			tradingCurrency.setCurrency(currency);
			tradingCurrency.setPrDescription(currency.getDescriptionEn());
			tradingCurrencyService.create(tradingCurrency);
		}
    }
}