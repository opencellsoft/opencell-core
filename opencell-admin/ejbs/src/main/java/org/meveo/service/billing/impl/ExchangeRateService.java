package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.util.ResourceBundle;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ExchangeRateDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.billing.ExchangeRate;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.base.PersistenceService;

@Stateless
public class ExchangeRateService extends PersistenceService<ExchangeRate> {
    
    @Inject
    protected ResourceBundle resourceMessages;
    
    @Inject
    protected TradingCurrencyService tradingCurrencyService;
    
    public ExchangeRate createCurrentRateWithPostData(ExchangeRateDto postData, TradingCurrency tradingCurrency) {
        if (postData.getFromDate() == null) {
            throw new MeveoApiException(resourceMessages.getString("error.exchangeRate.fromDate.empty"));
        } 
        // User cannot set a rate in a paste date
        if (postData.getFromDate().before(DateUtils.setTimeToZero(new Date()))) {
            throw new MeveoApiException(resourceMessages.getString("error.exchangeRate.fromDate.past"));
        }
        
        if (postData.getExchangeRate() == null || postData.getExchangeRate().compareTo(BigDecimal.ZERO) <= 0) {
            throw new MeveoApiException(resourceMessages.getString("error.exchangeRate.exchangeRate.incorrect"));
        }
        
        // Check if a user choose a date that is already taken
        if (findByfromDate(postData.getFromDate(), postData.getTradingCurrency().getId()) != null) {
            throw new BusinessApiException(resourceMessages.getString("error.exchangeRate.fromDate.isAlreadyTaken"));
        }
        
        ExchangeRate exchangeRate = new ExchangeRate();
        List<ExchangeRate> listExchangeRate = tradingCurrency.getExchangeRates();
        if (postData.getFromDate().compareTo(DateUtils.setTimeToZero(new Date())) == 0) {
            exchangeRate.setCurrentRate(true);
            for (ExchangeRate elementExchangeRate : listExchangeRate) {
                elementExchangeRate.setCurrentRate(false);
            }
            tradingCurrency.setExchangeRates(listExchangeRate);
            tradingCurrency.setCurrentRate(postData.getExchangeRate());
            tradingCurrency.setCurrentRateFromDate(DateUtils.setTimeToZero(new Date()));
            tradingCurrency.setCurrentRateUpdater(currentUser.getUserName());
        } else {
            exchangeRate.setCurrentRate(false);
        }
        exchangeRate.setTradingCurrency(tradingCurrency);
        exchangeRate.setExchangeRate(postData.getExchangeRate());
        exchangeRate.setFromDate(postData.getFromDate());
        create(exchangeRate);
        return exchangeRate;
    }
    
    public List<Long> getAllTradingCurrencyWithCurrentRate() {
        return getEntityManager()
                .createNamedQuery("ExchangeRate.getAllTradingCurrencyWithCurrentRate", Long.class)
                .setParameter("sysDate", DateUtils.setTimeToZero(new Date()))
                .getResultList();
    }
    
    public void updateCurrentRateForTradingCurrency(Long idExchangeRate) {
        ExchangeRate exchangeRate = findById(idExchangeRate);        
        TradingCurrency tradingCurrency = exchangeRate.getTradingCurrency();        
        List<ExchangeRate> listExchangeRate = tradingCurrency.getExchangeRates();
        
        for (ExchangeRate elementExchangeRate : listExchangeRate) {
            elementExchangeRate.setCurrentRate(false);
        }
        exchangeRate.setCurrentRate(true);
        tradingCurrency.setExchangeRates(listExchangeRate);
        tradingCurrency.setCurrentRate(exchangeRate.getExchangeRate());
        tradingCurrency.setCurrentRateFromDate(DateUtils.setTimeToZero(new Date()));
        tradingCurrency.setCurrentRateUpdater(currentUser.getUserName());
        exchangeRate.setTradingCurrency(tradingCurrency);
        update(exchangeRate);
    }    
    
    public ExchangeRate findByfromDate(Date fromDate, Long tradingCurrencyId) {        
        try {
            return (ExchangeRate) getEntityManager()
                    .createNamedQuery("ExchangeRate.findByfromDate", entityClass)
                    .setParameter("fromDate", fromDate)
                    .setParameter("tradingCurrencyId", tradingCurrencyId)
                    .getSingleResult();
        } catch (NoResultException e) {
            log.debug("No ExchangeRate entity found");
            return null;
        }
    }
    
    public void delete(Long id) throws MeveoApiException {
        ExchangeRate exchangeRate = findById(id);
        if(exchangeRate == null) {
            throw new EntityDoesNotExistsException(ExchangeRate.class, id);
        }
        // User cannot delete rate in a paste date
        if (exchangeRate.getFromDate().before(DateUtils.setTimeToZero(new Date()))) {
            throw new MeveoApiException(resourceMessages.getString("error.exchangeRate.delete.fromDate.past"));
        }
        
        try {
            remove(exchangeRate);
        } catch (Exception e) {
            throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, "Cannot delete entity");
        }
    }
}
