/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.admin.impl;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.service.base.PersistenceService;

@Stateless
@Named
public class TradingCurrencyService extends PersistenceService<TradingCurrency> {

    /**
     * Find TradingCurrency by its trading currency code.
     * 
     * @param tradingCurrencyCode Trading currency code
     * @return Trading currency found or null.
     * @throws ElementNotFoundException
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
}
