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

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.billing.EDRDto;
import org.meveo.cache.CdrEdrProcessingCacheContainerProvider;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.billing.RatedTransactionGroup;
import org.meveo.model.billing.Subscription;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.base.PersistenceService;

/**
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 */
@Stateless
public class EdrService extends PersistenceService<EDR> {

    @Inject
    private CdrEdrProcessingCacheContainerProvider cdrEdrProcessingCacheContainerProvider;

    @Inject
    private SubscriptionService subscriptionService;

    /**
     * Ways to check for EDR duplicates
     */
    public enum DeduplicateEDRTypeEnum {
        /**
         * Do not check for EDR duplicates
         */
        NONE,

        /**
         * Use cache to deduplicate EDRs
         */
        MEMORY,

        /**
         * Use database to deduplicate EDRS
         */
        DB;
    }

    static boolean deduplicateEdrs = false;
    static boolean useInMemoryDeduplication = false;
    static boolean inMemoryDeduplicationPrepopulated = false;

    @PostConstruct
    private void init() {
        ParamBean paramBean = ParamBeanFactory.getAppScopeInstance();

        String deduplicateType = paramBean.getProperty("mediation.deduplicate", EdrService.DeduplicateEDRTypeEnum.MEMORY.name());
        deduplicateEdrs = !EdrService.DeduplicateEDRTypeEnum.NONE.name().equalsIgnoreCase(deduplicateType);
        if (deduplicateEdrs) {
            useInMemoryDeduplication = EdrService.DeduplicateEDRTypeEnum.MEMORY.name().equalsIgnoreCase(deduplicateType);
            inMemoryDeduplicationPrepopulated = paramBean.getProperty("mediation.deduplicateInMemory.prepopulate", "true").equals("true");
        }
    }

    /**
     * Get a list of unprocessed EDRs to rate up to a given date. List is sorted by subscription and ID in ascending order
     * 
     * @param rateUntilDate date until we still rate
     * @param ratingGroup group of ratedTransaction. {@link RatedTransactionGroup}
     * @param nbToRetrieve Number of items to retrieve for processing
     * @return List of EDR's we can rate until a given date.
     */
    public List<Long> getEDRsToRate(Date rateUntilDate, String ratingGroup, int nbToRetrieve) {

        if (rateUntilDate == null && ratingGroup == null) {
            return getEntityManager().createNamedQuery("EDR.listToRateIds", Long.class).setMaxResults(nbToRetrieve).getResultList();

        } else if (rateUntilDate != null && ratingGroup == null) {
            return getEntityManager().createNamedQuery("EDR.listToRateIdsLimitByDate", Long.class).setParameter("rateUntilDate", rateUntilDate).setMaxResults(nbToRetrieve)
                .getResultList();

        } else if (rateUntilDate == null && ratingGroup != null) {
            return getEntityManager().createNamedQuery("EDR.listToRateIdsLimitByRG", Long.class).setParameter("ratingGroup", ratingGroup).setMaxResults(nbToRetrieve)
                .getResultList();

        } else {
            return getEntityManager().createNamedQuery("EDR.listToRateIdsLimitByDateAndRG", Long.class).setParameter("rateUntilDate", rateUntilDate)
                .setParameter("ratingGroup", ratingGroup).setMaxResults(nbToRetrieve).getResultList();
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
        if (!deduplicateEdrs) {
            return false;
        }
        Boolean isDuplicate = null;
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

        if (deduplicateEdrs && useInMemoryDeduplication) {
            cdrEdrProcessingCacheContainerProvider.setEdrDuplicationStatus(edr.getOriginBatch(), edr.getOriginRecord());
        }
    }

    /**
     * Reopen EDRs that were rejected
     * 
     * @param ids List of EDRs to reopen
     */
    public void reopenRejectedEDRS(List<Long> ids) {
        getEntityManager().createNamedQuery("EDR.reopenByIds").setParameter("ids", ids).executeUpdate();
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

    /**
     * Gets All not open EDR between two Date.
     *
     * @param firstTransactionDate first Transaction Date
     * @param lastTransactionDate last Transaction Date
     * @return All open EDR between two Date
     */
    public List<EDR> getNotOpenedEdrsBetweenTwoDates(Date firstTransactionDate, Date lastTransactionDate) {
        return getEntityManager().createNamedQuery("EDR.getNotOpenedEdrBetweenTwoDate", EDR.class).setParameter("firstTransactionDate", firstTransactionDate)
            .setParameter("lastTransactionDate", lastTransactionDate).getResultList();
    }

    /**
     * Remove All open EDR between two Date.
     *
     * @param firstTransactionDate first Transaction Date
     * @param lastTransactionDate last Transaction Date
     * @return the number of deleted entities
     */
    public long purge(Date firstTransactionDate, Date lastTransactionDate) {

        getEntityManager().createNamedQuery("EDR.updateWalletOperationForSafeDeletion").setParameter("firstTransactionDate", firstTransactionDate)
            .setParameter("lastTransactionDate", lastTransactionDate).executeUpdate();

        getEntityManager().createNamedQuery("EDR.updateRatedTransactionForSafeDeletion").setParameter("firstTransactionDate", firstTransactionDate)
            .setParameter("lastTransactionDate", lastTransactionDate).executeUpdate();

        return getEntityManager().createNamedQuery("EDR.deleteNotOpenEdrBetweenTwoDate").setParameter("firstTransactionDate", firstTransactionDate)
            .setParameter("lastTransactionDate", lastTransactionDate).executeUpdate();

    }

    public void importEdrs(List<EDRDto> edrs) throws BusinessException {
        for (EDRDto dto : edrs) {
            EDR edr = new EDR();
            if (dto.getSubscriptionCode() != null) {
                Subscription subscription = subscriptionService.findByCode(dto.getSubscriptionCode());
                edr.setSubscription(subscription);
            }

            edr.setOriginBatch(dto.getOriginBatch());
            edr.setOriginRecord(dto.getOriginRecord());
            edr.setEventDate(dto.getEventDate());
            edr.setQuantity(dto.getQuantity());
            edr.setParameter1(dto.getParameter1());
            edr.setParameter2(dto.getParameter2());
            edr.setParameter3(dto.getParameter3());
            edr.setParameter4(dto.getParameter4());
            edr.setParameter5(dto.getParameter5());
            edr.setParameter6(dto.getParameter6());
            edr.setParameter7(dto.getParameter7());
            edr.setParameter8(dto.getParameter8());
            edr.setParameter9(dto.getParameter9());
            edr.setDateParam1(dto.getDateParam1());
            edr.setDateParam2(dto.getDateParam2());
            edr.setDateParam3(dto.getDateParam3());
            edr.setDateParam4(dto.getDateParam4());
            edr.setDateParam5(dto.getDateParam5());
            edr.setDecimalParam1(dto.getDecimalParam1());
            edr.setDecimalParam2(dto.getDecimalParam2());
            edr.setDecimalParam3(dto.getDecimalParam3());
            edr.setDecimalParam4(dto.getDecimalParam4());
            edr.setDecimalParam5(dto.getDecimalParam5());
            edr.setCreated(dto.getCreated());
            edr.setLastUpdate(dto.getLastUpdate());
            edr.setAccessCode(dto.getAccessCode());
            edr.setExtraParameter(dto.getExtraParameter());
            if (dto.getStatus() != null && dto.getStatus() != EDRStatusEnum.OPEN) {
                edr.setStatus(dto.getStatus());
            }
            create(edr);
        }
    }
}