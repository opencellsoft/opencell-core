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
package org.meveo.service.catalog.impl;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;

/**
 * Calendar service implementation.
 */
@Stateless
@Named
public class CalendarService extends PersistenceService<Calendar> {

	public Calendar findByCode(String code, Provider provider) {
		return findByCode(getEntityManager(), code, provider);
	}

	public Calendar findByCode(EntityManager em, String code, Provider provider) {
		try {
			QueryBuilder qb = new QueryBuilder(Calendar.class, "c");
			qb.addCriterion("code", "=", code, true);
			qb.addCriterionEntity("c.provider", provider);

			return (Calendar) qb.getQuery(em).getSingleResult();
		} catch (NoResultException e) {
			log.warn(e.getMessage());
			return null;
		}
	}

	public Calendar findByCode(EntityManager em, String code) {
		try {
			QueryBuilder qb = new QueryBuilder(Calendar.class, "c");
			qb.addCriterion("code", "=", code, true);

			return (Calendar) qb.getQuery(em).getSingleResult();
		} catch (NoResultException e) {
			log.warn(e.getMessage());
			return null;
		}
	}
}