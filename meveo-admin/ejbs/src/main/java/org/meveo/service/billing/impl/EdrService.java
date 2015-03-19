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
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.cache.CdrEdrProcessingCacheContainerProvider;
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
	private CdrEdrProcessingCacheContainerProvider cdrEdrProcessingCacheContainerProvider;

	@Inject
	private Logger log;

	static boolean useInMemoryDeduplication = true;

	
	@PostConstruct
	private void init() {
		useInMemoryDeduplication = paramBean.getProperty("mediation.deduplicateInMemory", "true").equals("true");
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


    public boolean duplicateFound(Provider provider, String originBatch, String originRecord) {
        boolean result = false;
        if (useInMemoryDeduplication) {
            result = cdrEdrProcessingCacheContainerProvider.isEDRCached(provider.getId(), originBatch, originRecord);
        } else {
            result = findByBatchAndRecordId(originBatch, originRecord) != null;
        }
        return result;
    }

	public void create(EntityManager em, EDR edr, User user, Provider provider) {
		super.create(edr, user, provider);
		if (useInMemoryDeduplication) {
		    cdrEdrProcessingCacheContainerProvider.addEdrToCache(edr);
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

    /**
     * Get EDRs that are unprocessed
     * 
     * @param maxRecords Max records to retrieve
     * @return A list of EDR identifiers
     */
    public List<String> getUnprocessedEdrsForCache(int maxRecords) {
        return getEntityManager().createNamedQuery("EDR.getEdrsForCache", String.class).getResultList();
    }
}