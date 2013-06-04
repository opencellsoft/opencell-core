/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.service.billing.impl;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
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
	public TradingLanguage findByTradingLanguageCode(String tradingLanguageCode, Provider provider) {
		try {
			log.info("findByTradingLanguageCode tradingLanguageCode=#0,provider=#1",
					tradingLanguageCode, provider != null ? provider.getCode() : null);
			Query query = getEntityManager()
					.createQuery(
							"select b from TradingLanguage b where b.code = :tradingLanguageCode and b.provider=:provider");
			query.setParameter("tradingLanguageCode", tradingLanguageCode);
			query.setParameter("provider", provider);
			return (TradingLanguage) query.getSingleResult();
		} catch (NoResultException e) {
			log.warn(
					"findByTradingLanguageCode billing cycle not found : tradingLanguageCode=#0,provider=#1",
					tradingLanguageCode, provider != null ? provider.getCode() : null);
			return null;
		}
	}
}