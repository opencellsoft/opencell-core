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
package org.meveo.service.billing.impl;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;

@Stateless
@Named
@LocalBean
public class TradingCountryService extends PersistenceService<TradingCountry> {
	/**
	 * Find TradingCountry by its trading country code.
	 * 
	 * @param tradingCountryCode
	 *            Trading Country Code
	 * @return Trading country found or null.
	 * @throws ElementNotFoundException
	 */
	public TradingCountry findByTradingCountryCode(String tradingCountryCode,
			Provider provider) {
		try {
			log.info(
					"findByTradingCountryCode tradingCountryCode={},provider={}",
					tradingCountryCode, provider != null ? provider.getCode()
							: null);
			Query query = getEntityManager()
					.createQuery(
							"select b from TradingCountry b where b.country.countryCode = :tradingCountryCode and b.provider=:provider");
			query.setParameter("tradingCountryCode", tradingCountryCode);
			query.setParameter("provider", provider);
			return (TradingCountry) query.getSingleResult();
		} catch (NoResultException e) {
			log.warn(
					"findByTradingCountryCode billing cycle not found : tradingCountryCode={},provider={}",
					tradingCountryCode, provider != null ? provider.getCode()
							: null);
			return null;
		}
	}

	public TradingCountry findByTradingCountryCode(EntityManager em,
			String tradingCountryCode, Provider provider) {
		try {
			log.info(
					"findByTradingCountryCode tradingCountryCode={},provider={}",
					tradingCountryCode, provider != null ? provider.getCode()
							: null);
			Query query = em
					.createQuery("select b from TradingCountry b where b.country.countryCode = :tradingCountryCode and b.provider=:provider");
			query.setParameter("tradingCountryCode", tradingCountryCode);
			query.setParameter("provider", provider);
			return (TradingCountry) query.getSingleResult();
		} catch (NoResultException e) {
			log.warn(
					"findByTradingCountryCode billing cycle not found : tradingCountryCode={},provider={}",
					tradingCountryCode, provider != null ? provider.getCode()
							: null);
			return null;
		}
	}

}
