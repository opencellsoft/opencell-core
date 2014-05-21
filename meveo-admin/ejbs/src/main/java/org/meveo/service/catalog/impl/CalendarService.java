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
package org.meveo.service.catalog.impl;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.CalendarTypeEnum;
import org.meveo.service.base.PersistenceService;

/**
 * Calendar service implementation.
 */
@Stateless
@LocalBean
@Named
public class CalendarService extends PersistenceService<Calendar> {

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
