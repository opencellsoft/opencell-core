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
package org.meveo.service.crm.impl;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;

/**
 * Service Template service implementation.
 */
@Stateless
public class CustomerBrandService extends PersistenceService<CustomerBrand> {
	public CustomerBrand findByCode(String code) {

		try {
			return (CustomerBrand) getEntityManager()
					.createQuery(
							"from "
									+ CustomerBrand.class.getSimpleName()
									+ " where code=:code and provider=:provider")
					.setParameter("code", code)
					.setParameter("provider", getCurrentProvider())
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public CustomerBrand findByCode(EntityManager em, String code,
			Provider provider) {
		QueryBuilder qb = new QueryBuilder(CustomerBrand.class, "b");

		try {
			qb.addCriterion("code", "=", code, true);
			qb.addCriterionEntity("provider", provider);
			return (CustomerBrand) qb.getQuery(em).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}