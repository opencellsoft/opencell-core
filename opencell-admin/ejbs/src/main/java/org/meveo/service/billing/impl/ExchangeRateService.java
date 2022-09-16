package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.*;

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

import static java.math.BigDecimal.ONE;

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
            throw new MeveoApiException(resourceMessages.getString("The date must not be in the past"));
        }               
        
        if (postData.getExchangeRate() != null) {
            if (postData.getExchangeRate().compareTo(BigDecimal.ZERO) <= 0) {
                throw new MeveoApiException(resourceMessages.getString("error.exchangeRate.exchangeRate.incorrect"));
            }
            
            if (postData.getExchangeRate().compareTo(new BigDecimal("9999999999")) > 0) {
                throw new MeveoApiException(resourceMessages.getString("The exchange rate decimals must be limited to 6 digits and the fractional part to 9,999,999,999"));
            }
            
            BigDecimal fracExchangeRate = postData.getExchangeRate().subtract(new BigDecimal(postData.getExchangeRate().toBigInteger()));
            if (fracExchangeRate.toString().length() > 8) {
                throw new MeveoApiException(resourceMessages.getString("The exchange rate decimals must be limited to 6 digits and the fractional part to 9,999,999,999"));
            }
        }
        
        // Check if a user choose a date that is already taken
        if (findByFromDate(postData.getFromDate(), postData.getTradingCurrency().getId()) != null) {
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

        Set<ExchangeRate> exchangeRates = new HashSet<>(tradingCurrency.getExchangeRates());
        exchangeRates.add(exchangeRate);
        updateTradingCurrencyForExchangeRate(exchangeRate.getTradingCurrency(), exchangeRates);
        return exchangeRate;
    }

    public ExchangeRate createCurrentRateWithImpotFile(Date fromDate , BigDecimal exchangeRateValue , TradingCurrency tradingCurrency) {
        if (fromDate == null) {
            throw new MeveoApiException(resourceMessages.getString("error.exchangeRate.fromDate.empty"));
        } 
        // User cannot set a rate in a paste date
        if (fromDate.before(DateUtils.setTimeToZero(new Date()))) {
            throw new MeveoApiException(resourceMessages.getString("The date must not be in the past"));
        }               
        
        if (exchangeRateValue != null) {
            if (exchangeRateValue.compareTo(BigDecimal.ZERO) <= 0) {
                throw new MeveoApiException(resourceMessages.getString("error.exchangeRate.exchangeRate.incorrect"));
            }
            
            if (exchangeRateValue.compareTo(new BigDecimal("9999999999")) > 0) {
                throw new MeveoApiException(resourceMessages.getString("The exchange rate decimals must be limited to 6 digits and the fractional part to 9,999,999,999"));
            }
            
            BigDecimal fracExchangeRate = exchangeRateValue.subtract(new BigDecimal(exchangeRateValue.toBigInteger()));
            if (fracExchangeRate.toString().length() > 8) {
                throw new MeveoApiException(resourceMessages.getString("The exchange rate decimals must be limited to 6 digits and the fractional part to 9,999,999,999"));
            }
        }
        
        // Check if a user choose a date that is already taken
        if (findByFromDate(fromDate, tradingCurrency.getId()) != null) {
            throw new BusinessApiException(resourceMessages.getString("error.exchangeRate.fromDate.isAlreadyTaken"));
        }
        
        ExchangeRate exchangeRate = new ExchangeRate();
        List<ExchangeRate> listExchangeRate = tradingCurrency.getExchangeRates();
        if (fromDate.compareTo(DateUtils.setTimeToZero(new Date())) == 0) {
            exchangeRate.setCurrentRate(true);
            for (ExchangeRate elementExchangeRate : listExchangeRate) {
                elementExchangeRate.setCurrentRate(false);
            }
            tradingCurrency.setExchangeRates(listExchangeRate);
            tradingCurrency.setCurrentRate(exchangeRateValue);
            tradingCurrency.setCurrentRateFromDate(DateUtils.setTimeToZero(new Date()));
            tradingCurrency.setCurrentRateUpdater(currentUser.getUserName());
        } else {
            exchangeRate.setCurrentRate(false);
        }
        exchangeRate.setTradingCurrency(tradingCurrency);
        exchangeRate.setExchangeRate(exchangeRateValue);
        exchangeRate.setFromDate(fromDate);
        create(exchangeRate);

        Set<ExchangeRate> exchangeRates = new HashSet<>(tradingCurrency.getExchangeRates());
        exchangeRates.add(exchangeRate);
        updateTradingCurrencyForExchangeRate(exchangeRate.getTradingCurrency(), exchangeRates);
        return exchangeRate;
    }
    
    private void updateTradingCurrencyForExchangeRate(TradingCurrency tradingCurrency, Set<ExchangeRate> exchangeRates){

        if(tradingCurrency != null
                && (tradingCurrency.getCurrentRateFromDate() == null ||
                tradingCurrency.getCurrentRateFromDate().after(new Date()))){

             exchangeRates.stream()
                    .filter(exRate-> exRate.getFromDate().after(new Date()))
                    .min(Comparator.comparing(ExchangeRate::getFromDate))
                    .ifPresent(exchangeRate1->{

                TradingCurrency tradingCurrencyToUpdate = exchangeRate1.getTradingCurrency();
                tradingCurrencyToUpdate.setCurrentRate(exchangeRate1.getExchangeRate());
                tradingCurrencyToUpdate.setCurrentRateFromDate(exchangeRate1.getFromDate());
                tradingCurrencyToUpdate.setCurrentRateUpdater( exchangeRate1.getAuditable().getUpdater() != null ?
                        exchangeRate1.getAuditable().getUpdater() : exchangeRate1.getAuditable().getCreator()

                );

                tradingCurrencyService.update(tradingCurrencyToUpdate);
            });

        }


    }

    @Override
    public ExchangeRate update(ExchangeRate exchangeRate) {
        ExchangeRate updatedExchangeRate =  super.update(exchangeRate);
        Set<ExchangeRate> exchangeRates = new HashSet<>(exchangeRate.getTradingCurrency().getExchangeRates());
        exchangeRates.add(exchangeRate);
        updateTradingCurrencyForExchangeRate(exchangeRate.getTradingCurrency(), exchangeRates);
        return updatedExchangeRate;
    }

    public List<Long> getAllTradingCurrencyWithCurrentRate() {
        return getEntityManager()
                .createNamedQuery("ExchangeRate.getAllTradingCurrencyWithCurrentRate", Long.class)
                .setParameter("sysDate", DateUtils.setTimeToZero(new Date()))
                .getResultList();
    }
    
    public void updateCurrentRateForTradingCurrency(Long exchangeRateId) {
        ExchangeRate exchangeRate = findById(exchangeRateId);
        TradingCurrency tradingCurrency = exchangeRate.getTradingCurrency();
        if(tradingCurrency != null) {
            if(tradingCurrency.getCurrentRate().equals(ONE)) {
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
            } else {
                tradingCurrencyService.updateFunctionalCurrency(tradingCurrency);
            }
        }
    }
    
    public ExchangeRate findByFromDate(Date fromDate, Long tradingCurrencyId) {
        try {
            return getEntityManager()
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
            Set<ExchangeRate> exchangeRates = new HashSet<>(exchangeRate.getTradingCurrency().getExchangeRates());
            exchangeRates.remove(exchangeRate);
            updateTradingCurrencyForExchangeRate(exchangeRate.getTradingCurrency(), exchangeRates);
        } catch (Exception e) {
            throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, "Cannot delete entity");
        }
    }
}
