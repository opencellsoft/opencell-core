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
package org.meveo.service.reporting.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;

import org.meveo.model.datawarehouse.DWHAccountOperation;
import org.meveo.service.base.PersistenceService;

/**
 * Account Operation Transformation service implementation.
 * 
 */
@Stateless
public class DWHAccountOperationService extends PersistenceService<DWHAccountOperation> {

    /**
     * @param from from month
     * @param to to month
     * @param category category of dwh
     * @return records between months
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public BigDecimal calculateRecordsBetweenDueMonth(Integer from, Integer to, String category) {
        log.info("calculateRecordsBetweenDueMonth({},{},{})", new Object[] {from, to, category });
        BigDecimal result = new BigDecimal(0);
        StringBuilder queryString = new StringBuilder("select sum(unMatchingAmount) from ")
                .append(getEntityClass().getSimpleName())
                .append(" where category= :category and (status=0 or status=2)");

        if (from != null) {
            queryString.append(" and dueMonth >= :from");
        }
        if (to != null) {
            queryString.append(" and dueMonth < :to");
        }
        log.debug("calculateRecordsBetweenDueMonth: queryString={}", queryString);
        Query query = getEntityManager().createQuery(queryString.toString()).setParameter("category", category);
        if (from != null) {
            query.setParameter("from", from);
        }
        if (to != null) {
            query.setParameter("to", to);
        }
        if (query.getSingleResult() != null) {
            result = (BigDecimal) query.getSingleResult();
        }
        log.info("calculateRecordsBetweenDueMonth: {}", result);
        return result;
    }

    /**
     * @param from from month
     * @param to to month
     * @param category category of dwh
     * @return number of records
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public int countRecordsBetweenDueMonth(Integer from, Integer to, String category) {
        log.info("countRecordsBetweenDueMonth({},{},{})", new Object[] { from, to, category });
        int result = 0;
        StringBuilder queryString = new StringBuilder("select count(*) from ")
                .append(getEntityClass().getSimpleName())
                .append(" where category= :category and (status=0 or status=2)");
        if (from != null) {
            queryString.append(" and dueMonth >= :from");
        }
        if (to != null) {
            queryString.append(" and dueMonth < :to");
        }
        log.debug("countRecordsBetweenDueMonth: queryString={}", queryString);
        Query query = getEntityManager().createQuery(queryString.toString()).setParameter("category", category);
        log.debug("countRecordsBetweenDueMonth: query={}", query);
        Object queryResult = query.getSingleResult();
        if (from != null) {
            query.setParameter("from", from);
        }
        if (to != null) {
            query.setParameter("to", to);
        }
        if (queryResult != null) {
            result = ((Long) queryResult).intValue();
        }
        log.info("countRecordsBetweenDueMonth: {}", result);
        return result;
    }

	/**
	 * @param category category of dwh
	 * @return total amount of records.
	 */
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public BigDecimal totalAmount(String category) {
		log.info("totalAmount({})", category);
		BigDecimal result = new BigDecimal(0);
		StringBuilder queryString = new StringBuilder("select sum(unMatchingAmount) from ")
		        .append(getEntityClass().getSimpleName())
		        .append(" where category= :category and status=0  or status=2");
		log.debug("totalAmount: queryString={}", queryString);
		Query query = getEntityManager().createQuery(queryString.toString()).setParameter("category", category);
		log.debug("countRecordsBetweenDueMonth: query={}", query);
		Object queryResult = query.getSingleResult();
		if (queryResult != null) {
			result = (BigDecimal) queryResult;
		}
		log.info("totalAmount: {}", result);
		return result;
	}

	/**
	 * @param category category of dwh
	 * @return total count.
	 */
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public int totalCount(String category) {
		log.info("totalCount({})", category);
		int result = 0;
		StringBuilder queryString = new StringBuilder("select count(*) from ")
		        .append(getEntityClass().getSimpleName())
		        .append(" where category= :category and status=0  or status=2");
		log.debug("totalCount: queryString={}", queryString);
		Query query = getEntityManager().createQuery(queryString.toString()).setParameter("category", category);
		log.debug("totalCount: query={}", query);
		Object queryResult = query.getSingleResult();
		if (queryResult != null) {
			result = ((Long) queryResult).intValue();
		}
		log.info("totalCount: {}", result);
		return result;
	}

	/**
	 * @param endDate ending date
	 * @return list of dwh account operation.
	 */
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<DWHAccountOperation> getAccountingDetailRecords(Date endDate) {
		List<DWHAccountOperation> result = null;
		log.info("getAccountingDetailRecords( {} )", endDate);
		Query query = getEntityManager()
				.createQuery(
						"from "
								+ getEntityClass().getSimpleName()
								+ " a where (a.status=0 or a.status=2) and"
								+ " a.transactionDate <= :endDate order by a.accountCode,a.transactionDate")
				.setParameter("endDate", endDate);
		log.debug("getAccountingDetailRecords: query={}", query);
		result = query.getResultList();
		log.info("getAccountingDetailRecords: {} records", result);
		return result;
	}

	/**
	 * @param startDate starting date
	 * @param endDate ending date
	 * @return list of dwh account operation.
	 */
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<DWHAccountOperation> getAccountingJournalRecords(
			Date startDate, Date endDate) {
		List<DWHAccountOperation> result = null;
		log.info("getAccountingDetailRecords( {}, {})", startDate, endDate);
		Query query = getEntityManager()
				.createQuery(
						"from "
								+ getEntityClass().getSimpleName()
								+ " a where a.type<>1 and a.transactionDate>=:startDate and"
								+ " a.transactionDate <= :endDate order by a.transactionDate,a.accountCode,a.occCode")
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate);
		log.debug("getAccountingDetailRecords: query={}", query);
		result = query.getResultList();
		log.info("getAccountingDetailRecords: {} records", result);
		return result;
	}

	/**
	 * @param endDate ending date
	 * @param category category
	 * @return list of accounting summary records
	 */
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Object> getAccountingSummaryRecords(Date endDate, int category) {
		List<Object> result = null;
		log.info("getAccountingSummaryRecords( {}, {} )", endDate, category);
		Query query = getEntityManager()
				.createQuery(
						"select a.occCode, a.occDescription, sum(unMatchingAmount) as amount from "
								+ getEntityClass().getSimpleName()
								+ " a where (a.status=0 or a.status=2) and a.category = :category and a.transactionDate <= :endDate  group by a.occCode, a.occDescription order by a.occCode")
				.setParameter("endDate", endDate)
				.setParameter("category", (byte) category);
		log.debug("getAccountingSummaryRecords: query={}", query);
		result = query.getResultList();
		log.info("getAccountingSummaryRecords: {} records", result);
		return result;
	}

	/**
	 * @param startDate starting date
	 * @param endDate ending date
	 * @return list of objects
	 */
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Object> getObjectsForSIMPAC(
			Date startDate, Date endDate) {
		List<Object> result = null;
		log.info("getObjectsForSIMPAC( {}, {})", startDate, endDate);
		Query query = getEntityManager()
				.createQuery(
						"select a.accountingCode,a.accountingCodeClientSide,sum(a.amount*(1-2*a.category)) as amount from "
								+ getEntityClass().getSimpleName()
								+ " a where a.type<>1 "
								+ "and  a.transactionDate>=:startDate and a.transactionDate <= :endDate  "
								+ "group by  a.accountingCode,a.accountingCodeClientSide order by a.accountingCode")
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate);
		log.debug("getObjectsForSIMPAC: query={}", query);
		result = query.getResultList();
		log.info("getObjectsForSIMPAC: {} records", result);
		return result;
	}
}
