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

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;

/**
 * Service SubscriptionTerminationReason implementation.
 */
@Named
@Stateless
public class SubscriptionTerminationReasonService extends
		PersistenceService<SubscriptionTerminationReason> {

	public SubscriptionTerminationReason findByCodeReason(String codeReason,
			Provider provider) throws Exception {
		return findByCodeReason(getEntityManager(), codeReason, provider);
	}

	public SubscriptionTerminationReason findByCodeReason(EntityManager em,
			String codeReason, Provider provider) throws Exception {
		QueryBuilder qb = new QueryBuilder(SubscriptionTerminationReason.class,
				"r");

		qb.addCriterion("codeReason", "=", codeReason, true);
		qb.addCriterionEntity("provider", provider);

		try {
			return (SubscriptionTerminationReason) qb.getQuery(em)
					.getSingleResult();
		} catch (NoResultException e) {
			log.warn(e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<SubscriptionTerminationReason> listReasons() {

		Query query = new QueryBuilder(SubscriptionTerminationReason.class,
				"c", null, getCurrentProvider()).getQuery(getEntityManager());
		return query.getResultList();
	}
}
