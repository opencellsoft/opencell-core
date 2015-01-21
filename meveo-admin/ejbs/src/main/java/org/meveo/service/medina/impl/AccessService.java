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
package org.meveo.service.medina.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.Subscription;
import org.meveo.model.mediation.Access;
import org.meveo.service.base.PersistenceService;

@Stateless
public class AccessService extends PersistenceService<Access> {

	@SuppressWarnings("unchecked")
	public List<Access> findByUserID(String userId) {
		log.info("findByUserID '" + userId + "'");
		List<Access> result = new ArrayList<Access>();
		if (userId != null && userId.length() > 0) {
			Query query = getEntityManager().createQuery("from Access a where a.accessUserId=:accessUserId")
					.setParameter("accessUserId", userId);
			result = query.getResultList();
		}
		return result;
	}

	public boolean isDuplicate(Access access) {
		String stringQuery = "SELECT COUNT(*) FROM " + Access.class.getName()
				+ " a WHERE a.accessUserId=:accessUserId AND a.subscription.id=:subscriptionId";
		Query query = getEntityManager().createQuery(stringQuery);
		query.setParameter("accessUserId", access.getAccessUserId());
		query.setParameter("subscriptionId", access.getSubscription().getId());
		query.setHint("org.hibernate.flushMode", "NEVER");
		return ((Long) query.getSingleResult()).intValue() != 0;
	}

	public Access findByUserIdAndSubscription(String accessUserId, Subscription subscription) {
		return findByUserIdAndSubscription(getEntityManager(), accessUserId, subscription);
	}

	public Access findByUserIdAndSubscription(EntityManager em, String accessUserId, Subscription subscription) {
		try {
			QueryBuilder qb = new QueryBuilder(Access.class, "a");
			qb.addCriterion("accessUserId", "=", accessUserId, false);
			qb.addCriterionEntity("subscription", subscription);
			return (Access) qb.getQuery(em).getSingleResult();
		} catch (NoResultException e) {
			log.warn("no result found");
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Access> listBySubscription(Subscription subscription) {
		QueryBuilder qb = new QueryBuilder(Access.class, "c");
		qb.addCriterionEntity("subscription", subscription);

		try {
			return (List<Access>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			log.warn(e.getMessage());
			return null;
		}
	}

}
