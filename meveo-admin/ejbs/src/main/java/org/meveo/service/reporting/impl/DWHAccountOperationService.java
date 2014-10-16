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
package org.meveo.service.reporting.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
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
@LocalBean
public class DWHAccountOperationService extends PersistenceService<DWHAccountOperation> {

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public BigDecimal calculateRecordsBetweenDueMonth(String providerCode, Integer from,
			Integer to, String category) {
		log.info("calculateRecordsBetweenDueMonth({},{},{})", new Object[] { from, to, category });
		BigDecimal result = new BigDecimal(0);
		String queryString = "select sum(unMatchingAmount) from "
				+ getEntityClass().getSimpleName() + " where providerCode='" + providerCode
				+ "' and category=" + category + " and (status=0 or status=2)";
		;
		if (from != null) {
			queryString += " and dueMonth >= " + from;
		}
		if (to != null) {
			queryString += " and dueMonth < " + to;
		}
		log.debug("calculateRecordsBetweenDueMonth: queryString={}", queryString);
		Query query = getEntityManager().createQuery(queryString);
		if (query.getSingleResult() != null) {
			result = (BigDecimal) query.getSingleResult();
		}
		log.info("calculateRecordsBetweenDueMonth: {}", result);
		return result;
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public int countRecordsBetweenDueMonth(String providerCode, Integer from, Integer to,
			String category) {
		log.info("countRecordsBetweenDueMonth({},{},{})", new Object[] { from, to, category });
		int result = 0;
		String queryString = "select count(*) from " + getEntityClass().getSimpleName()
				+ " where providerCode='" + providerCode + "' and category=" + category
				+ " and (status=0 or status=2)";
		if (from != null) {
			queryString += " and dueMonth >= " + from;
		}
		if (to != null) {
			queryString += " and dueMonth < " + to;
		}
		log.debug("countRecordsBetweenDueMonth: queryString={}", queryString);
		Query query = getEntityManager().createQuery(queryString);
		log.debug("countRecordsBetweenDueMonth: query={}", query);
		Object queryResult = query.getSingleResult();
		if (queryResult != null) {
			result = ((Long) queryResult).intValue();
		}
		log.info("countRecordsBetweenDueMonth: {}", result);
		return result;
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public BigDecimal totalAmount(String providerCode, String category) {
		log.info("totalAmount({})", category);
		BigDecimal result = new BigDecimal(0);
		String queryString = "select sum(unMatchingAmount) from "
				+ getEntityClass().getSimpleName() + " where providerCode='" + providerCode
				+ "' and category=" + category + " and status=0  or status=2";
		log.debug("totalAmount: queryString={}", queryString);
		Query query = getEntityManager().createQuery(queryString);
		log.debug("countRecordsBetweenDueMonth: query={}", query);
		Object queryResult = query.getSingleResult();
		if (queryResult != null) {
			result = (BigDecimal) queryResult;
		}
		log.info("totalAmount: {}", result);
		return result;
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public int totalCount(String providerCode, String category) {
		log.info("totalCount({})", category);
		int result = 0;
		String queryString = "select count(*) from " + getEntityClass().getSimpleName()
				+ " where providerCode='" + providerCode + "' and category=" + category
				+ " and status=0  or status=2";
		log.debug("totalCount: queryString={}", queryString);
		Query query = getEntityManager().createQuery(queryString);
		log.debug("totalCount: query={}", query);
		Object queryResult = query.getSingleResult();
		if (queryResult != null) {
			result = ((Long) queryResult).intValue();
		}
		log.info("totalCount: {}", result);
		return result;
	}

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<DWHAccountOperation> getAccountingDetailRecords(String providerCode, Date endDate) {
		List<DWHAccountOperation> result = null;
		log.info("getAccountingDetailRecords( {} )", endDate);
		Query query = getEntityManager()
				.createQuery(
						"from "
								+ getEntityClass().getSimpleName()
								+ " a where a.providerCode='"
								+ providerCode
								+ "' and (a.status=0 or a.status=2) and"
								+ " a.transactionDate <= :endDate order by a.accountCode,a.transactionDate")
				.setParameter("endDate", endDate);
		log.debug("getAccountingDetailRecords: query={}", query);
		result = query.getResultList();
		log.info("getAccountingDetailRecords: {} records", result);
		return result;
	}

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<DWHAccountOperation> getAccountingJournalRecords(String providerCode,
			Date startDate, Date endDate) {
		List<DWHAccountOperation> result = null;
		log.info("getAccountingDetailRecords( {}, {})", startDate, endDate);
		Query query = getEntityManager()
				.createQuery(
						"from "
								+ getEntityClass().getSimpleName()
								+ " a where a.providerCode='"
								+ providerCode
								+ "'  and a.type<>1 and a.transactionDate>=:startDate and"
								+ " a.transactionDate <= :endDate order by a.transactionDate,a.accountCode,a.occCode")
				.setParameter("startDate", startDate).setParameter("endDate", endDate);
		log.debug("getAccountingDetailRecords: query={}", query);
		result = query.getResultList();
		log.info("getAccountingDetailRecords: {} records", result);
		return result;
	}

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Object> getAccountingSummaryRecords(String providerCode, Date endDate, int category) {
		List<Object> result = null;
		log.info("getAccountingSummaryRecords( {}, {} )", endDate, category);
		Query query = getEntityManager()
				.createQuery(
						"select a.occCode, a.occDescription, sum(unMatchingAmount) as amount from "
								+ getEntityClass().getSimpleName()
								+ " a where a.providerCode='"
								+ providerCode
								+ "' and (a.status=0 or a.status=2) and a.category = :category and a.transactionDate <= :endDate  group by a.occCode, a.occDescription order by a.occCode")
				.setParameter("endDate", endDate).setParameter("category", (byte) category);
		log.debug("getAccountingSummaryRecords: query={}", query);
		result = query.getResultList();
		log.info("getAccountingSummaryRecords: {} records", result);
		return result;
	}

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Object> getObjectsForSIMPAC(String providerCode, Date startDate, Date endDate) {
		List<Object> result = null;
		log.info("getObjectsForSIMPAC( {}, {})", startDate, endDate);
		Query query = getEntityManager()
				.createQuery(
						"select a.accountingCode,a.accountingCodeClientSide,sum(a.amount*(1-2*a.category)) as amount from "
								+ getEntityClass().getSimpleName()
								+ " a where a.providerCode='"
								+ providerCode
								+ "' and a.type<>1 "
								+ "and  a.transactionDate>=:startDate and a.transactionDate <= :endDate  "
								+ "group by  a.accountingCode,a.accountingCodeClientSide order by a.accountingCode")
				.setParameter("startDate", startDate).setParameter("endDate", endDate);
		log.debug("getObjectsForSIMPAC: query={}", query);
		result = query.getResultList();
		log.info("getObjectsForSIMPAC: {} records", result);
		return result;
	}
}
