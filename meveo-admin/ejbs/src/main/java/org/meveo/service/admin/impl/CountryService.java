/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
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

@Stateless
@Named
public class CountryService extends PersistenceService<Country> {

	@Inject
	private CurrencyService currencyService;

	@Inject
	private UserService userService;

	public Country findByCode(String countryCode) {
		return findByCode(getEntityManager(), countryCode);
	}

	public Country findByCode(EntityManager em, String countryCode) {
		if (countryCode == null || countryCode.trim().length()==0) {
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
