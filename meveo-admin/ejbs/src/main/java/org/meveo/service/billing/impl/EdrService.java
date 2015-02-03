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
package org.meveo.service.billing.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.infinispan.api.BasicCache;
import org.infinispan.manager.CacheContainer;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.billing.Subscription;
import org.meveo.model.crm.Provider;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.base.PersistenceService;
import org.slf4j.Logger;

@Stateless
public class EdrService extends PersistenceService<EDR> {

	ParamBean paramBean = ParamBean.getInstance();

	@Inject
	private Logger log;

	static boolean useInMemoryDeduplication = true;
	static int maxDuplicateRecords = 100000;

	@Resource(name = "java:jboss/infinispan/container/meveo")
	private CacheContainer meveoContainer;

	private static BasicCache<String, Integer> duplicateCache;

	private void loadCacheFromDB() {
		synchronized (duplicateCache) {
			loadDeduplicationInfo(maxDuplicateRecords);
		}
	}

	@PostConstruct
	private void init() {
		useInMemoryDeduplication = paramBean.getProperty("mediation.deduplicateInMemory", "true").equals("true");
		if (useInMemoryDeduplication) {
			int newMaxDuplicateRecords = Integer.parseInt(paramBean.getProperty("mediation.deduplicateCacheSize",
					"100000"));
			if (newMaxDuplicateRecords != maxDuplicateRecords && duplicateCache != null) {
				// duplicateCache.setMaxSize(newMaxDuplicateRecords); //never
				// used
			}
			maxDuplicateRecords = newMaxDuplicateRecords;
			if (duplicateCache == null) {
				duplicateCache = meveoContainer.getCache("meveo-edr-cache");
				loadCacheFromDB();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<EDR> getEDRToRate(Provider provider) {
		QueryBuilder qb = new QueryBuilder(EDR.class, "e");
		qb.addCriterionEntity("provider", provider);
		qb.addCriterion("status", "=", EDRStatusEnum.OPEN, true);

		try {
			return (List<EDR>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public EDR findByBatchAndRecordId(String originBatch, String originRecord) {
		EDR result = null;
		try {
			Query query = getEntityManager()
					.createQuery("from EDR e where e.originBatch=:originBatch and e.originRecord=:originRecord")
					.setParameter("originBatch", originBatch).setParameter("originRecord", originRecord);
			result = (EDR) query.getSingleResult();
		} catch (Exception e) {
		}
		return result;
	}

	public void loadDeduplicationInfo(int maxDuplicateRecords) {
		duplicateCache.clear();
		Query query = getEntityManager()
				.createQuery(
						"select CONCAT(e.originBatch,e.originRecord) from EDR e where e.status=:status ORDER BY e.eventDate DESC")
				.setParameter("status", EDRStatusEnum.OPEN).setMaxResults(maxDuplicateRecords);
		@SuppressWarnings("unchecked")
		List<String> results = query.getResultList();
		for (String edrHash : results) {
			duplicateCache.put(edrHash, 0);
		}
	}

	public boolean duplicateFound(String originBatch, String originRecord) {
		boolean result = false;
		if (useInMemoryDeduplication) {
			result = duplicateCache.containsKey(originBatch + originRecord);
		} else {
			result = findByBatchAndRecordId(originBatch, originRecord) != null;
		}
		return result;
	}

	public void create(EntityManager em, EDR e, User user, Provider provider) {
		super.create(e, user, provider);
		if (useInMemoryDeduplication) {
			synchronized (duplicateCache) {
				duplicateCache.put(e.getOriginBatch() + e.getOriginRecord(), 0);
			}
		}
	}

	public void massUpdate(EDRStatusEnum status, Subscription subscription, Provider provider) {
		StringBuilder sb = new StringBuilder();

		sb.append("UPDATE "
				+ EDR.class.getSimpleName()
				+ " e SET e.status=:newStatus, e.lastUpdate=:lastUpdate WHERE e.status=:oldStatus AND e.subscription=:subscription AND e.provider=:provider");

		try {
			getEntityManager().createQuery(sb.toString()).setParameter("newStatus", status)
					.setParameter("subscription", subscription).setParameter("oldStatus", EDRStatusEnum.REJECTED)
					.setParameter("provider", provider).setParameter("lastUpdate", new Date()).executeUpdate();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	public void massUpdate(EDRStatusEnum status, Set<Long> selectedIds, Provider provider) {
		StringBuilder sb = new StringBuilder();

		sb.append("UPDATE "
				+ EDR.class.getSimpleName()
				+ " e SET e.status=:newStatus, e.lastUpdate=:lastUpdate WHERE e.status=:oldStatus AND e.id IN :selectedIds AND e.provider=:provider");

		try {
			log.debug(
					"{} rows updated",
					getEntityManager().createQuery(sb.toString()).setParameter("newStatus", status)
							.setParameter("selectedIds", selectedIds).setParameter("oldStatus", EDRStatusEnum.REJECTED)
							.setParameter("provider", provider).setParameter("lastUpdate", new Date()).executeUpdate());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
}
