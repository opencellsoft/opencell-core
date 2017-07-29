/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.billing.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.cache.CdrEdrProcessingCacheContainerProvider;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.Subscription;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.base.PersistenceService;

@Stateless
public class EdrService extends PersistenceService<EDR> {

	ParamBean paramBean = ParamBean.getInstance();
	
	@Inject
	private CdrEdrProcessingCacheContainerProvider cdrEdrProcessingCacheContainerProvider;

	static boolean useInMemoryDeduplication = true;

	
	@PostConstruct
	private void init() {
		useInMemoryDeduplication = paramBean.getProperty("mediation.deduplicateInMemory", "true").equals("true");
	}
	
	public List<Long> getEDRidsToRate() {
		QueryBuilder qb = new QueryBuilder(EDR.class, "c");
		qb.addCriterion("c.status", "=", EDRStatusEnum.OPEN, true);

		try {
			return qb.getIdQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<EDR> getEDRToRate() {
		QueryBuilder qb = new QueryBuilder(EDR.class, "e");
		
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


    public boolean duplicateFound(String originBatch, String originRecord) {
        boolean result = false;
        if (useInMemoryDeduplication) {
            result = cdrEdrProcessingCacheContainerProvider.isEDRCached(originBatch, originRecord);
        } else {
            result = findByBatchAndRecordId(originBatch, originRecord) != null;
        }
        return result;
    }

    @Override
	public void create(EDR edr) throws BusinessException {
		super.create(edr);
		if (useInMemoryDeduplication) {
		    cdrEdrProcessingCacheContainerProvider.addEdrToCache(edr);
		}
	}

	public void massUpdate(EDRStatusEnum status, Subscription subscription) {
		StringBuilder sb = new StringBuilder();

		sb.append("UPDATE "
				+ EDR.class.getSimpleName()
				+ " e SET e.status=:newStatus, e.lastUpdate=:lastUpdate WHERE e.status=:oldStatus AND e.subscription=:subscription");

		try {
			getEntityManager().createQuery(sb.toString()).setParameter("newStatus", status)
					.setParameter("subscription", subscription).setParameter("oldStatus", EDRStatusEnum.REJECTED)
					.setParameter("lastUpdate", new Date()).executeUpdate();
			
		} catch (Exception e) {
			log.error("error while updating edr",e);
		}
	}

	public void massUpdate(EDRStatusEnum status, Set<Long> selectedIds) {
		StringBuilder sb = new StringBuilder();

		sb.append("UPDATE "
				+ EDR.class.getSimpleName()
				+ " e SET e.status=:newStatus, e.lastUpdate=:lastUpdate WHERE e.status=:oldStatus AND e.id IN :selectedIds ");

		try {
			log.debug(
					"{} rows updated",
					getEntityManager().createQuery(sb.toString()).setParameter("newStatus", status)
							.setParameter("selectedIds", selectedIds).setParameter("oldStatus", EDRStatusEnum.REJECTED)
							.setParameter("lastUpdate", new Date()).executeUpdate());
		} catch (Exception e) {
			log.error("failed to updating edr",e);
		}
	}

    /**
     * Get EDRs that are unprocessed. Sorted in ascending order, limited to a number of items to return as configured in 'mediation.deduplicateCacheSize' setting
     * 
     * @return A list of EDR identifiers
     */
    public List<String> getUnprocessedEdrsForCache() {
        int maxRecords = Integer.parseInt(paramBean.getProperty("mediation.deduplicateCacheSize", "100000"));

        List<String> edrCacheKeys = getEntityManager().createNamedQuery("EDR.getEdrsForCache", String.class).setMaxResults(maxRecords).getResultList();
        // Reverse the list, so the latest records, would be later in the list
        Collections.reverse(edrCacheKeys);

        return edrCacheKeys;
    }
}