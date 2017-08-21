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
package org.meveo.service.billing.impl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.TradingCountry;
import org.meveo.service.base.PersistenceService;

@Stateless
public class TradingCountryService extends PersistenceService<TradingCountry> {
	/**
	 * Find TradingCountry by its trading country code.
	 * 
	 * @param tradingCountryCode
	 *            Trading Country Code
	 * @return Trading country found or null.
	 * @throws ElementNotFoundException
	 */
	public TradingCountry findByTradingCountryCode(String tradingCountryCode) {
		try {
			log.info("findByTradingCountryCode tradingCountryCode={}", tradingCountryCode);
			QueryBuilder qb = new QueryBuilder(TradingCountry.class, "c");
			qb.addCriterion("c.country.countryCode", "=", tradingCountryCode, true);
			return (TradingCountry) qb.getQuery(getEntityManager()).getSingleResult();

		} catch (NoResultException e) {
			log.warn("findByTradingCountryCode billing cycle not found : tradingCountryCode={}", tradingCountryCode);
			return null;
		}
	}
}
