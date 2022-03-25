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
package org.meveo.service.billing.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.model.billing.ExchangeRate;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.base.PersistenceService;

@Stateless
public class ExchangeRateService extends PersistenceService<ExchangeRate> {
    
    @Inject
    protected TradingCurrencyService tradingCurrencyService;
    
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
    
    
    public boolean fromDateExists(Date fromDate) {
        return getEntityManager()
                .createNamedQuery("ExchangeRate.countByFromDate", Long.class)
                .setParameter("fromDate", fromDate)
                .getSingleResult() > 0;
    }
}
