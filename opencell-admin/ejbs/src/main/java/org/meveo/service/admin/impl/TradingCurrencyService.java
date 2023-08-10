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
package org.meveo.service.admin.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;

import org.apache.commons.collections4.CollectionUtils;
import org.meveo.model.billing.ExchangeRate;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.ExchangeRateService;
import org.meveo.service.billing.impl.InvoiceService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ONE;
import static java.util.Objects.isNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.meveo.model.shared.DateUtils.setTimeToZero;

@Stateless
@Named
public class TradingCurrencyService extends PersistenceService<TradingCurrency> {

    @Inject
    private ExchangeRateService exchangeRateService;
    @Inject
    private InvoiceService invoiceService;

    /**
     * Find TradingCurrency by its trading currency code.
     *
     * @param tradingCurrencyCode Trading currency code
     * @return Trading currency found or null.
     */
    public TradingCurrency findByTradingCurrencyCode(String tradingCurrencyCode) {

        try {
            return getEntityManager().createNamedQuery("TradingCurrency.getByCode", TradingCurrency.class).setParameter("tradingCurrencyCode", tradingCurrencyCode)
                    .getSingleResult();

        } catch (NoResultException e) {
            log.warn("Trading currency not found : currency={}", tradingCurrencyCode);
            return null;
        }
    }

    public static String getCurrencySymbol(String code) {
        try {
            return Currency.getInstance(code).getSymbol();
        } catch (IllegalArgumentException e) {
            return code;
        }
    }

    /**
     * Refresh trading currency information
     * @param tradingCurrency
     * @return updated trading currency
     */
    public TradingCurrency updateFunctionalCurrency(TradingCurrency tradingCurrency) {
        tradingCurrency.setCurrentRate(ONE);
        tradingCurrency.setCurrentRateFromDate(new Date());
        tradingCurrency.setCurrentRateUpdater(currentUser.getUserName());
        Date today = setTimeToZero(new Date());
        tradingCurrency.getExchangeRates().stream()
                .filter(exchangeRate -> !isNull(exchangeRate.getTradingCurrency()) && exchangeRate.isCurrentRate())
                .forEach(exchangeRate -> exchangeRate.setExchangeRate(ONE));
        List<ExchangeRate> rateToRemove = tradingCurrency.getExchangeRates()
                .stream()
                .filter(exchangeRate -> exchangeRate.getFromDate().compareTo(today) >= 0).collect(Collectors.toList());

        of(rateToRemove).orElse(Collections.emptyList())
                .forEach(exchangeRate -> exchangeRateService.remove(exchangeRate));

        return update(tradingCurrency);
    }

    public void checkFunctionalCurrency(boolean isCleanAppliedRateInvoice) {
        // check functional currency rate : US INTRD-8094
        // on doit avoir toujours fuctionalCurrency rate = 1 sans aucun ExchangeRate fromDate sup à now()
        if (appProvider.getCurrency() != null) {
            TradingCurrency tradingCurrency = findByTradingCurrencyCode(appProvider.getCurrency().getCurrencyCode());
            Date today = setTimeToZero(new Date());
            if (tradingCurrency != null && (tradingCurrency.getCurrentRate().compareTo(BigDecimal.ONE) != 0 ||
                    CollectionUtils.isNotEmpty(tradingCurrency.getExchangeRates().stream().filter(exchangeRate -> exchangeRate.getFromDate().compareTo(today) >= 0)
                            .collect(Collectors.toList())))) {
                log.warn("Functional currency shall have rate always as 1, otherwise, we should update it [functionCurrency={}, tradingCurrency={}]",
                        appProvider.getCurrency().getId(), tradingCurrency.getId());
                updateFunctionalCurrency(tradingCurrency);
            }
        }

        if (isCleanAppliedRateInvoice) {
            log.info("Start clean Invoice wich use FunctionalCurrency but there rate is different from 1");
            invoiceService.cleanUpInvoiceWithFuntionalCurrencyDifferentFromOne();
        }
    }
    
    public TradingCurrency findByTradingCurrencyCodeOrId(String tradingCurrencyCode, Long tradingCurrencyId) {
	
	        try {
	            return getEntityManager().createNamedQuery("TradingCurrency.getByCodeOrId", TradingCurrency.class)
	            							.setParameter("tradingCurrencyCode", tradingCurrencyCode)
	            							.setParameter("tradingCurrencyId", tradingCurrencyId)
	                    .getSingleResult();
	
	        } catch (NoResultException e) {
	            log.warn("Trading currency not found : currency={}", tradingCurrencyCode);
	            return null;
	        }
	    }

    /**
     * Find trading currency by currency id
     *
     * @param currencyId currency id
     * @return Optional<TradingCurrency>
     */
    public Optional<TradingCurrency> findByTradingCurrencyByCurrencyID(Long currencyId) {
        try {
            return of(getEntityManager().createNamedQuery("TradingCurrency.getTradingFromCurrency", TradingCurrency.class)
                    .setParameter("currencyID", currencyId)
                    .getSingleResult());
        } catch (NoResultException e) {
            log.warn("No Trading currency associated to currency={}", currencyId);
            return empty();
        }
    }
}
