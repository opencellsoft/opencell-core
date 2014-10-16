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
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;

@Stateless
@LocalBean
public class TradingLanguageService extends PersistenceService<TradingLanguage> {
	/**
	 * Find TradingLanguage by its trading language code.
	 * 
	 * @param tradingLanguageCode
	 *            Trading Language Code
	 * @return Trading language found or null.
	 * @throws ElementNotFoundException
	 */
	public TradingLanguage findByTradingLanguageCode(
			String tradingLanguageCode, Provider provider) {
		return findByTradingLanguageCode(getEntityManager(),
				tradingLanguageCode, provider);
	}

	public TradingLanguage findByTradingLanguageCode(EntityManager em,
			String tradingLanguageCode, Provider provider) {
		try {
			log.debug(
					"findByTradingLanguageCode tradingLanguageCode={},provider={}",
					tradingLanguageCode, provider != null ? provider.getCode()
							: null);
			Query query = em
					.createQuery("select b from TradingLanguage b where b.language.languageCode = :tradingLanguageCode and b.provider=:provider");
			query.setParameter("tradingLanguageCode", tradingLanguageCode);
			query.setParameter("provider", provider);
			return (TradingLanguage) query.getSingleResult();
		} catch (NoResultException e) {
			log.warn(
					"findByTradingLanguageCode not found : tradingLanguageCode={},provider={}",
					tradingLanguageCode, provider != null ? provider.getCode()
							: null);
			return null;
		}
	}
}
