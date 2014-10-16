/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.admin.impl;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;

@Stateless
@Named
@LocalBean
public class TradingCurrencyService extends PersistenceService<TradingCurrency> {
	public TradingCurrency findByTradingCurrencyCode(
			String tradingCurrencyCode, Provider provider) {
		return findByTradingCurrencyCode(getEntityManager(),
				tradingCurrencyCode, provider);
	}

	public TradingCurrency findByTradingCurrencyCode(EntityManager em,
			String tradingCurrencyCode, Provider provider) {
		try {
			log.info(
					"findByTradingCurrencyCode tradingCurrencyCode={},provider={}",
					tradingCurrencyCode, provider != null ? provider.getCode()
							: null);
			Query query = em
					.createQuery("select b from TradingCurrency b where b.currency.currencyCode = :tradingCurrencyCode and b.provider=:provider");
			query.setParameter("tradingCurrencyCode", tradingCurrencyCode);
			query.setParameter("provider", provider);
			return (TradingCurrency) query.getSingleResult();
		} catch (NoResultException e) {
			log.warn(
					"findByTradingCurrencyCode not found : tradingCurrencyCode={},provider={}",
					tradingCurrencyCode, provider != null ? provider.getCode()
							: null);
			return null;
		}
	}
}
