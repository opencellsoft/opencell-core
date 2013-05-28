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
package org.meveo.service.admin.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.Country;
import org.meveo.service.base.PersistenceService;

@Stateless
@Named
public class CountryService extends PersistenceService<Country> {
	public Country findByCode(String countryCode) {
		log.debug("start of find {} by code (code={}) ..", getEntityClass().getSimpleName(),
				countryCode);
		StringBuilder queryString = new StringBuilder("from " + Country.class.getName() + " a");
		queryString.append(" where a.countryCode = :countryCode");
		Query query = em.createQuery(queryString.toString());
		query.setParameter("countryCode", countryCode);
		if (query.getResultList().size() == 0) {
			return null;
		}
		Country e = (Country) query.getSingleResult();

		log.debug("end of find {} by code (code={}). Result found={}.", getEntityClass()
				.getSimpleName(), countryCode, e != null);

		return e;
	}

	@SuppressWarnings("unchecked")
	public List<Country> list() {
		QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", null, getCurrentProvider());
		queryBuilder.addOrderCriterion("a.descriptionEn", true);
		Query query = queryBuilder.getQuery(em);
		return query.getResultList();
	}
}
