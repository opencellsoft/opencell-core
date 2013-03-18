/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.service.reporting.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Named;
import javax.persistence.Query;

import org.meveo.model.datawarehouse.DWHAccountOperation;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.reporting.local.DWHAccountOperationServiceLocal;

/**
 * Account Operation Transformation service implementation.
 * 
 */
@Stateless
@Named
public class DWHAccountOperationService extends PersistenceService<DWHAccountOperation> implements
		DWHAccountOperationServiceLocal {

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public BigDecimal calculateRecordsBetweenDueMonth(String providerCode, Integer from,
			Integer to, String category) {
		log.info("calculateRecordsBetweenDueMonth({0},{1},{2})", from, to, category);
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
		log.debug("calculateRecordsBetweenDueMonth: queryString={0}", queryString);
		Query query = dwhEntityManager.createQuery(queryString);
		if (query.getSingleResult() != null) {
			result = (BigDecimal) query.getSingleResult();
		}
		log.info("calculateRecordsBetweenDueMonth: {0}", result);
		return result;
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public int countRecordsBetweenDueMonth(String providerCode, Integer from, Integer to,
			String category) {
		log.info("countRecordsBetweenDueMonth({0},{1},{2})", from, to, category);
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
		log.debug("countRecordsBetweenDueMonth: queryString={0}", queryString);
		Query query = dwhEntityManager.createQuery(queryString);
		log.debug("countRecordsBetweenDueMonth: query={0}", query);
		Object queryResult = query.getSingleResult();
		if (queryResult != null) {
			result = ((Long) queryResult).intValue();
		}
		log.info("countRecordsBetweenDueMonth: {0}", result);
		return result;
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public BigDecimal totalAmount(String providerCode, String category) {
		log.info("totalAmount({0})", category);
		BigDecimal result = new BigDecimal(0);
		String queryString = "select sum(unMatchingAmount) from "
				+ getEntityClass().getSimpleName() + " where providerCode='" + providerCode
				+ "' and category=" + category + " and status=0  or status=2";
		log.debug("totalAmount: queryString={0}", queryString);
		Query query = dwhEntityManager.createQuery(queryString);
		log.debug("countRecordsBetweenDueMonth: query={0}", query);
		Object queryResult = query.getSingleResult();
		if (queryResult != null) {
			result = (BigDecimal) queryResult;
		}
		log.info("totalAmount: {0}", result);
		return result;
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public int totalCount(String providerCode, String category) {
		log.info("totalCount({0})", category);
		int result = 0;
		String queryString = "select count(*) from " + getEntityClass().getSimpleName()
				+ " where providerCode='" + providerCode + "' and category=" + category
				+ " and status=0  or status=2";
		log.debug("totalCount: queryString={0}", queryString);
		Query query = dwhEntityManager.createQuery(queryString);
		log.debug("totalCount: query={0}", query);
		Object queryResult = query.getSingleResult();
		if (queryResult != null) {
			result = ((Long) queryResult).intValue();
		}
		log.info("totalCount: {0}", result);
		return result;
	}

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<DWHAccountOperation> getAccountingDetailRecords(String providerCode, Date endDate) {
		List<DWHAccountOperation> result = null;
		log.info("getAccountingDetailRecords( {0} )", endDate);
		Query query = dwhEntityManager
				.createQuery(
						"from "
								+ getEntityClass().getSimpleName()
								+ " a where a.providerCode='"
								+ providerCode
								+ "' and (a.status=0 or a.status=2) and"
								+ " a.transactionDate <= :endDate order by a.accountCode,a.transactionDate")
				.setParameter("endDate", endDate);
		log.debug("getAccountingDetailRecords: query={0}", query);
		result = query.getResultList();
		log.info("getAccountingDetailRecords: {0} records", result);
		return result;
	}

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<DWHAccountOperation> getAccountingJournalRecords(String providerCode,
			Date startDate, Date endDate) {
		List<DWHAccountOperation> result = null;
		log.info("getAccountingDetailRecords( {0}, {1})", startDate, endDate);
		Query query = dwhEntityManager
				.createQuery(
						"from "
								+ getEntityClass().getSimpleName()
								+ " a where a.providerCode='"
								+ providerCode
								+ "'  and a.type<>1 and a.transactionDate>=:startDate and"
								+ " a.transactionDate <= :endDate order by a.transactionDate,a.accountCode,a.occCode")
				.setParameter("startDate", startDate).setParameter("endDate", endDate);
		log.debug("getAccountingDetailRecords: query={0}", query);
		result = query.getResultList();
		log.info("getAccountingDetailRecords: {0} records", result);
		return result;
	}

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Object> getAccountingSummaryRecords(String providerCode, Date endDate, int category) {
		List<Object> result = null;
		log.info("getAccountingSummaryRecords( {0}, {1} )", endDate, category);
		Query query = dwhEntityManager
				.createQuery(
						"select a.occCode, a.occDescription, sum(unMatchingAmount) as amount from "
								+ getEntityClass().getSimpleName()
								+ " a where a.providerCode='"
								+ providerCode
								+ "' and (a.status=0 or a.status=2) and a.category = :category and a.transactionDate <= :endDate  group by a.occCode, a.occDescription order by a.occCode")
				.setParameter("endDate", endDate).setParameter("category", (byte) category);
		log.debug("getAccountingSummaryRecords: query={0}", query);
		result = query.getResultList();
		log.info("getAccountingSummaryRecords: {0} records", result);
		return result;
	}

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Object> getObjectsForSIMPAC(String providerCode, Date startDate, Date endDate) {
		List<Object> result = null;
		log.info("getObjectsForSIMPAC( {0}, {1})", startDate, endDate);
		Query query = dwhEntityManager
				.createQuery(
						"select a.accountingCode,a.accountingCodeClientSide,sum(a.amount*(1-2*a.category)) as amount from "
								+ getEntityClass().getSimpleName()
								+ " a where a.providerCode='"
								+ providerCode
								+ "' and a.type<>1 "
								+ "and  a.transactionDate>=:startDate and a.transactionDate <= :endDate  "
								+ "group by  a.accountingCode,a.accountingCodeClientSide order by a.accountingCode")
				.setParameter("startDate", startDate).setParameter("endDate", endDate);
		log.debug("getObjectsForSIMPAC: query={0}", query);
		result = query.getResultList();
		log.info("getObjectsForSIMPAC: {0} records", result);
		return result;
	}
}
