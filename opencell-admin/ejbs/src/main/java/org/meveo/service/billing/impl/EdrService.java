/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.service.billing.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.billing.EDRDto;
import org.meveo.cache.CdrEdrProcessingCacheContainerProvider;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.BaseEntity;
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
    
	private static final int PURGE_MAX_RESULTS = 30000;

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

            StringBuilder selectQuery = new StringBuilder("select e.id from EDR e where ")
                    .append(originBatch == null ? "e.originBatch is null " : "e.originBatch=:originBatch")
                    .append(" and ")
                    .append(originRecord == null ? "e.originRecord is null " : "e.originRecord=:originRecord");
                            
            Query query = getEntityManager().createQuery(selectQuery.toString());

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
     * Update edrs to reprocess.
     *
     * @param ids the ids
     */
    public void updateEdrsToReprocess(List<Long> ids) {
        getEntityManager().createNamedQuery("EDR.updateEdrsToReprocess").setParameter("ids", ids).executeUpdate();
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
     * @param lastId a last id used for pagination
     * @param max  a max rows
     * @return All open EDR between two Date
     */
    public List<EDR> getNotOpenedEdrsBetweenTwoDates(Date firstTransactionDate, Date lastTransactionDate, long lastId, int max) {
        return getEntityManager().createNamedQuery("EDR.getNotOpenedEdrBetweenTwoDate", EDR.class)
                .setParameter("firstTransactionDate", firstTransactionDate)
                .setParameter("lastTransactionDate", lastTransactionDate)
                .setParameter("lastId", lastId)
                .setMaxResults(max)
                .getResultList();
    }

    /**
     * Remove All open EDR between two Date.
     *
     * @param firstTransactionDate first Transaction Date
     * @param lastTransactionDate  last Transaction Date
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
            edr.setUpdated(dto.getLastUpdate());
            edr.setAccessCode(dto.getAccessCode());
            edr.setExtraParameter(dto.getExtraParameter());
            if (dto.getStatus() != null && dto.getStatus() != EDRStatusEnum.OPEN) {
                edr.setStatus(dto.getStatus());
            }
            create(edr);
        }
    }

	/**
	 * @param firstTransactionDate
	 * @param lastTransactionDate
	 * @param lastId
	 * @param maxResult
	 * @param formattedStatus
	 * @return
	 */
	public List<EDR> getEdrsBetweenTwoDatesByStatus(Date firstTransactionDate, Date lastTransactionDate, long lastId, int maxResult, List<EDRStatusEnum> formattedStatus) {
		return getEntityManager().createNamedQuery("EDR.getEdrsBetweenTwoDateByStatus", EDR.class)
				.setParameter("status", formattedStatus)
                .setParameter("firstTransactionDate", firstTransactionDate)
                .setParameter("lastTransactionDate", lastTransactionDate)
                .setParameter("lastId", lastId)
                .setMaxResults(maxResult)
                .getResultList();
	}

	public long purge(Date lastTransactionDate, List<EDRStatusEnum> targetStatusList) {
        getEntityManager().createNamedQuery("EDR.updateWalletOperationForSafeDeletionByStatusV1").setParameter("status", targetStatusList)
            .setParameter("lastTransactionDate", lastTransactionDate).executeUpdate();

        getEntityManager().createNamedQuery("EDR.updateRatedTransactionForSafeDeletionByStatusV1").setParameter("status", targetStatusList)
            .setParameter("lastTransactionDate", lastTransactionDate).executeUpdate();

        return getEntityManager().createNamedQuery("EDR.deleteEdrByLastTransactionDateAndStatus").setParameter("status", targetStatusList)
            .setParameter("lastTransactionDate", lastTransactionDate).executeUpdate();
    }

	public long purge(Date firstTransactionDate, Date lastTransactionDate, List<EDRStatusEnum> targetStatusList) {
		getEntityManager().createNamedQuery("EDR.updateWalletOperationForSafeDeletionByStatus").setParameter("status", targetStatusList).setParameter("firstTransactionDate", firstTransactionDate)
				.setParameter("lastTransactionDate", lastTransactionDate).executeUpdate();

		getEntityManager().createNamedQuery("EDR.updateRatedTransactionForSafeDeletionByStatus").setParameter("status", targetStatusList).setParameter("firstTransactionDate", firstTransactionDate)
				.setParameter("lastTransactionDate", lastTransactionDate).executeUpdate();

		return getEntityManager().createNamedQuery("EDR.deleteEdrBetweenTwoDateByStatus").setParameter("status", targetStatusList).setParameter("firstTransactionDate", firstTransactionDate)
				.setParameter("lastTransactionDate", lastTransactionDate).executeUpdate();
	}
	
	public <T extends Enum<T>, E extends BaseEntity> long countMediationDataToPurge(String firstTransactionDate, List<T> targetStatus, Class<E> clazz) {
		if (targetStatus != null && !targetStatus.isEmpty()) {
			String sqlString = buildSelectOrCountMediationDataToPurgeQuery(firstTransactionDate, targetStatus, clazz, true, 0);
			if(sqlString != null) {
				log.debug("count Mediation Data To Purge query : {}", sqlString);
				getEntityManager().clear();
				return ((BigInteger) getEntityManager().createNativeQuery(sqlString).getSingleResult()).longValue();
			}
		}
		return 0l;
	}
	
	private <T extends Enum<T>, E extends BaseEntity> String buildSelectOrCountMediationDataToPurgeQuery(String firstTransactionDate, List<T> targetStatus, Class<E> clazz, boolean countQuery, long lastId) {
		
		String tablelName = null;
		String dateColName = null;
		
		switch (clazz.getSimpleName()) {
		case "WalletOperation":
			tablelName = "billing_wallet_operation";
			dateColName = "operation_date";
			break;
		case "RatedTransaction":
			tablelName = "billing_rated_transaction";
			dateColName = "usage_date";
			break;
		case "EDR":
			tablelName = "rating_edr";
			dateColName = "event_date";
			break;

		default:
			return null;
		}
		
		StringBuilder queryBuilder = new StringBuilder("select ");
		queryBuilder.append(countQuery ? "count(*) " : "id ");
		queryBuilder.append("from ").append(tablelName);
		queryBuilder.append(" where ");
		
		if(!countQuery) {
			queryBuilder.append("id > ").append(lastId).append(" and ");
		}
			
		queryBuilder
			.append("date(")
			.append(dateColName)
			.append(") = '")
			.append(firstTransactionDate)
			.append("'\\:\\:DATE ")
			.append(statusListToSqlClause(targetStatus));
		
		if(!countQuery) {
			queryBuilder.append(" order by id asc");
		}
		
		return queryBuilder.toString();
	}
	
	public <T extends Enum<T>> String statusListToSqlClause(List<T> enumsList) {
		StringBuilder inClauseBuilder = new StringBuilder("");
		if (enumsList != null && !enumsList.isEmpty()) {
			inClauseBuilder.append(" and status ").append(enumsList.size() > 1 ? "in (" : "= ");
			boolean firstTime = true;
			for (T status : enumsList) {
				if (!firstTime) {
					inClauseBuilder.append(",");
				}
				inClauseBuilder.append("'").append(status.name()).append("'");
				firstTime = false;
			}
			inClauseBuilder.append(enumsList.size() > 1 ? ")" : "");
		}
		return inClauseBuilder.toString();
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Enum<T>, E extends BaseEntity> List<BigInteger> getMediationDataIdsToPurge(String firstTransactionDate, List<T> targetStatus, Class<E> clazz, long lastId) {
		if (targetStatus != null && !targetStatus.isEmpty()) {
			String sqlString = buildSelectOrCountMediationDataToPurgeQuery(firstTransactionDate, targetStatus, clazz, false, lastId);
			if(sqlString != null) {
				log.debug("get Mediation Data Ids To Purge query : {}", sqlString);
				getEntityManager().clear();
				return (List<BigInteger>) getEntityManager().createNativeQuery(sqlString).setMaxResults(PURGE_MAX_RESULTS).getResultList();
			}
		}
		return new ArrayList<BigInteger>();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public <E extends BaseEntity> long purgeMediationDataPacket(List<BigInteger> ids, Class<E> clazz) {
		
		String inClause = buildInIdsClause(ids);

		switch (clazz.getSimpleName()) {
		
		case "WalletOperation":
			
			String q10 = new StringBuilder("update billing_wallet_operation set reratedwalletoperation_id = null where reratedwalletoperation_id in (").append(inClause).append(")").toString();
			String q7 = new StringBuilder("delete from billing_wallet_operation where id in (").append(inClause).append(")").toString();
			
			long r10 = getEntityManager().createNativeQuery(q10).executeUpdate();
			log.debug("{} rows updated \n with query {}", r10, q10);
			
			long r7 = getEntityManager().createNativeQuery(q7).executeUpdate();
			log.debug("{} rows deleted \n with query {}", r7, q7);
			
			return r7;
			
		case "RatedTransaction":
			
			String q8 = new StringBuilder("update billing_rated_transaction set adjusted_rated_tx = null where adjusted_rated_tx in (").append(inClause).append(")").toString();
			long r8 = getEntityManager().createNativeQuery(q8).executeUpdate();
			log.debug("{} rows updated  \n with query {}", r8, q8);
			
			String q11 = new StringBuilder("update billing_wallet_operation set rated_transaction_id = null where rated_transaction_id in (").append(inClause).append(")").toString();
			long r11 = getEntityManager().createNativeQuery(q8).executeUpdate();
			log.debug("{} rows updated  \n with query {}", r11, q11);
			
			String q9 = new StringBuilder("delete from billing_rated_transaction where id in (").append(inClause).append(")").toString();
			long r9 = getEntityManager().createNativeQuery(q9).executeUpdate();
			log.debug("{} rows deleted \n with query {}", r9, q9);
			
			return r9;
		
		case "EDR":
			
			String q1 = new StringBuilder("update billing_wallet_operation set edr_id = null where edr_id in (").append(inClause).append(")").toString();
			String q2 = new StringBuilder("update billing_rated_transaction set edr_id = null where edr_id in (").append(inClause).append(")").toString();
			String q3 = new StringBuilder("update billing_reservation set origin_edr_id = null where origin_edr_id in (").append(inClause).append(")").toString();
			String q4 = new StringBuilder("update rating_edr set header_edr_id = null where header_edr_id in (").append(inClause).append(")").toString();
			String q5 = new StringBuilder("delete from rating_edr where id in (").append(inClause).append(")").toString();
			
			long r1 = getEntityManager().createNativeQuery(q1).executeUpdate();
			log.debug("{} rows updated \n with query {}", r1, q1);
			
			long r2 = getEntityManager().createNativeQuery(q2).executeUpdate();
			log.debug("{} rows updated \n with query {}", r2, q2);
			
			long r3 = getEntityManager().createNativeQuery(q3).executeUpdate();
			log.debug("{} rows updated \n with query {}", r3, q3);
			
			long r4 = getEntityManager().createNativeQuery(q4).executeUpdate();
			log.debug("{} rows updated \n with query {}", r4, q4);
			
			long r5 = getEntityManager().createNativeQuery(q5).executeUpdate();
			log.debug("{} rows deleted \n with query {}", r5, q5);
			
			return r5;

		default:
			return 0l;
		}

	}
	
	private String buildInIdsClause(List<BigInteger> ids) {
		StringBuilder in = new StringBuilder();
		boolean fisrtTime = true;
		for(BigInteger id : ids) {
			if(!fisrtTime)
				in.append(",");
			in.append(id.longValue());
			fisrtTime =false;
		}
		return in.toString();
	}

}