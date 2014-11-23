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

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.CalendarTypeEnum;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;

/**
 * Calendar service implementation.
 */
@Stateless
@Named
public class CalendarService extends PersistenceService<Calendar> {

	public Calendar findByName(String name, Provider provider) {
		return findByName(getEntityManager(), name, provider);
	}

	public Calendar findByName(EntityManager em, String name, Provider provider) {
		try {
			QueryBuilder qb = new QueryBuilder(Calendar.class, "c");
			qb.addCriterion("name", "=", name, true);
			qb.addCriterionEntity("c.provider", provider);

			return (Calendar) qb.getQuery(em).getSingleResult();
		} catch (NoResultException e) {
			log.warn(e.getMessage());
			return null;
		}
	}

	public Calendar findByName(EntityManager em, String name) {
		try {
			QueryBuilder qb = new QueryBuilder(Calendar.class, "c");
			qb.addCriterion("name", "=", name, true);

			return (Calendar) qb.getQuery(em).getSingleResult();
		} catch (NoResultException e) {
			log.warn(e.getMessage());
			return null;
		}
	}

	/**
	 * @see org.meveo.service.catalog.local.CalendarServiceLocal#listChargeApplicationCalendars()
	 */
	@SuppressWarnings("unchecked")
	public List<Calendar> listChargeApplicationCalendars() {
		Query query = new QueryBuilder(Calendar.class, "c", null,
				getCurrentProvider()).addCriterionEnum("type",
				CalendarTypeEnum.CHARGE_IMPUTATION)
				.getQuery(getEntityManager());
		return query.getResultList();
	}

	/**
	 * @see org.meveo.service.catalog.local.CalendarServiceLocal#listBillingCalendars()
	 */
	@SuppressWarnings("unchecked")
	public List<Calendar> listBillingCalendars() {
		Query query = new QueryBuilder(Calendar.class, "c", null,
				getCurrentProvider()).addCriterionEnum("type",
				CalendarTypeEnum.BILLING).getQuery(getEntityManager());
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Calendar> listCounterCalendars() {
		Query query = new QueryBuilder(Calendar.class, "c", null,
				getCurrentProvider()).addCriterionEnum("type",
				CalendarTypeEnum.COUNTER).getQuery(getEntityManager());
		return query.getResultList();
	}

}
