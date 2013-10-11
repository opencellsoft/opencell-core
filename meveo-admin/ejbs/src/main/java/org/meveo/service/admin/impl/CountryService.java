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

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.Auditable;
import org.meveo.model.admin.User;
import org.meveo.model.billing.Country;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.crm.impl.ProviderService;

@Stateless
@Named
public class CountryService extends PersistenceService<Country> {

	@Inject
	private CurrencyService currencyService;

	@Inject
	private UserService userService;

	@Inject
	private ProviderService providerService;

	public Country findByCode(String countryCode) {
		return findByCode(getEntityManager(), countryCode);
	}

	public Country findByCode(EntityManager em, String countryCode) {
		if (countryCode == null) {
			return null;
		}

		QueryBuilder qb = new QueryBuilder(Country.class, "c");
		qb.addCriterion("countryCode", "=", countryCode, false);

		try {
			return (Country) qb.getQuery(em).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Country> list() {
		QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", null,
				getCurrentProvider());
		queryBuilder.addOrderCriterion("a.descriptionEn", true);
		Query query = queryBuilder.getQuery(getEntityManager());
		return query.getResultList();
	}

	public void create(Long userId, String countryCode, String name,
			String currencyCode) {
		User creator = userService.findById(userId);

		Country c = new Country();

		Auditable auditable = new Auditable();
		auditable.setCreated(new Date());
		auditable.setCreator(creator);

		c.setAuditable(auditable);
		c.setCountryCode(countryCode);
		c.setDescriptionEn(name);
		c.setCurrency(currencyService.findByCode(currencyCode));

		create(c, creator);
	}

}
