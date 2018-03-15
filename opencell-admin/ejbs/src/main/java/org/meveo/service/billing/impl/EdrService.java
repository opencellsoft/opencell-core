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

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.cache.CdrEdrProcessingCacheContainerProvider;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.Subscription;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.base.PersistenceService;

@Stateless
public class EdrService extends PersistenceService<EDR> {

    @Inject
    private CdrEdrProcessingCacheContainerProvider cdrEdrProcessingCacheContainerProvider;

    // static boolean useInMemoryDeduplication = true;
    // static boolean inMemoryDeduplicationPrepopulated = false;

    // @PostConstruct
    // private void init() {
    // paramBean = paramBeanFactory.getInstance();
    // useInMemoryDeduplication = paramBean.getProperty("mediation.deduplicateInMemory", "true").equals("true");
    // inMemoryDeduplicationPrepopulated = paramBean.getProperty("mediation.deduplicateInMemory.prepopulate", "false").equals("true");
    // }

    /**
     * Get a list of unprocessed EDRs to rate up to a given date. List is sorted by subscription and ID in ascending order
     * 
     * @param rateUntilDate date until we still rate
     * @return list of EDR'sId we can rate until a given date.
     */
    public List<Long> getEDRidsToRate(Date rateUntilDate) {
        QueryBuilder qb = new QueryBuilder(EDR.class, "c");
        qb.addCriterion("c.status", "=", EDRStatusEnum.OPEN, true);
        if (rateUntilDate != null) {
            qb.addCriterion("c.eventDate", "<", rateUntilDate, false);
        }
        qb.addOrderDoubleCriterion("subscription", true, "id", true);

        try {
            return qb.getIdQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Check if EDR exits matching an origin batch and record numbers
     * 
     * @param originBatch original batch
     * @param originRecord origin record
     * @return True if EDR was found
     */
    public boolean isEDRExistsByBatchAndRecordId(String originBatch, String originRecord) {

        try {

            Query query = getEntityManager().createQuery("select e.id from EDR e where " + (originBatch == null ? "e.originBatch is null " : "e.originBatch=:originBatch") + " and "
                    + (originRecord == null ? "e.originRecord is null " : "e.originRecord=:originRecord"));

            if (originBatch != null) {
                query.setParameter("originBatch", originBatch);
            }
            if (originRecord != null) {
                query.setParameter("originRecord", originRecord);
            }
            query.getSingleResult();
            return true;

        } catch (NoResultException e) {
            return false;

        } catch (NonUniqueResultException e) {
            return true;
        }
    }

    /**
     * Check if EDR, identified by batch and a record, was processed already
     * 
     * @param originBatch original batch
     * @param originRecord original record
     * @return true/false
     */
    public boolean isDuplicateFound(String originBatch, String originRecord) {
        Boolean isDuplicate = null;
        boolean useInMemoryDeduplication = paramBeanFactory.getInstance().getProperty("mediation.deduplicateInMemory", "true").equals("true");
        boolean inMemoryDeduplicationPrepopulated = paramBeanFactory.getInstance().getProperty("mediation.deduplicateInMemory.prepopulate", "false").equals("true");
        if (useInMemoryDeduplication) {
            isDuplicate = cdrEdrProcessingCacheContainerProvider.getEdrDuplicationStatus(originBatch, originRecord);
            if (isDuplicate == null && !inMemoryDeduplicationPrepopulated) {
                isDuplicate = isEDRExistsByBatchAndRecordId(originBatch, originRecord);
                // cdrEdrProcessingCacheContainerProvider.setEdrDuplicationStatus(originBatch, originRecord, isDuplicate); // no need to set as it will be added to cache once EDR
                // is processed
            } else if (isDuplicate == null) {
                isDuplicate = false;
            }
        } else {
            isDuplicate = isEDRExistsByBatchAndRecordId(originBatch, originRecord);
        }
        return isDuplicate;
    }

    @Override
    public void create(EDR edr) throws BusinessException {
        super.create(edr);
        boolean useInMemoryDeduplication = paramBeanFactory.getInstance().getProperty("mediation.deduplicateInMemory", "true").equals("true");
        if (useInMemoryDeduplication) {
            cdrEdrProcessingCacheContainerProvider.setEdrDuplicationStatus(edr.getOriginBatch(), edr.getOriginRecord());
        }
    }

    /**
     * @param status EDR status
     * @param subscription subscription in which EDR is updating.
     */
    public void massUpdate(EDRStatusEnum status, Subscription subscription) {
        StringBuilder sb = new StringBuilder();

        sb.append("UPDATE " + EDR.class.getSimpleName() + " e SET e.status=:newStatus, e.lastUpdate=:lastUpdate WHERE e.status=:oldStatus AND e.subscription=:subscription");

        try {
            getEntityManager().createQuery(sb.toString()).setParameter("newStatus", status).setParameter("subscription", subscription)
                .setParameter("oldStatus", EDRStatusEnum.REJECTED).setParameter("lastUpdate", new Date()).executeUpdate();

        } catch (Exception e) {
            log.error("error while updating edr", e);
        }
    }

    /**
     * @param status EDR status
     * @param selectedIds list of selected EDR ids
     */
    public void massUpdate(EDRStatusEnum status, Set<Long> selectedIds) {
        StringBuilder sb = new StringBuilder();

        sb.append("UPDATE " + EDR.class.getSimpleName() + " e SET e.status=:newStatus, e.lastUpdate=:lastUpdate WHERE e.status=:oldStatus AND e.id IN :selectedIds ");

        try {
            log.debug("{} rows updated", getEntityManager().createQuery(sb.toString()).setParameter("newStatus", status).setParameter("selectedIds", selectedIds)
                .setParameter("oldStatus", EDRStatusEnum.REJECTED).setParameter("lastUpdate", new Date()).executeUpdate());
        } catch (Exception e) {
            log.error("failed to updating edr", e);
        }
    }

    /**
     * Get EDRs that are unprocessed. Sorted in descending order by event date, so older items will be added first and thus expire first from the cache, limited to a number of
     * items to return as configured in 'mediation.deduplicateCacheSize' setting
     * 
     * @param from Pagination - a record to retrieve from
     * @param pageSize Pagination - number of records to retrieve
     * @return A list of EDR identifiers
     */
    public List<String> getUnprocessedEdrsForCache(int from, int pageSize) {

        List<String> edrCacheKeys = getEntityManager().createNamedQuery("EDR.getEdrsForCache", String.class).setFirstResult(from).setMaxResults(pageSize).getResultList();

        return edrCacheKeys;
    }
}