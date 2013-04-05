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

import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;

import org.meveo.model.datawarehouse.JournalEntry;
import org.meveo.service.base.PersistenceService;

/**
 * Sales Transformation service implementation.
 * 
 */
@Stateless @LocalBean
public class JournalEntryService extends PersistenceService<JournalEntry> {

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Object> getTaxRecodsBetweenDate(String providerCode, Date startDate, Date endDate) {
		List<Object> result = null;
		log.info("getTaxRecodsBetweenDate ( {0}, {1})", startDate, endDate);
		Query query = dwhEntityManager
				.createQuery(
						"select a.taxCode, a.taxDescription, a.taxPercent, sum(amountWithoutTax) as amountWithoutTax,  sum(amountTax) as amountTax from "
								+ getEntityClass().getSimpleName()
								+ " a where a.providerCode=:providerCode and a.type='T' and a.invoiceDate>=:startDate and a.invoiceDate <=:endDate group by a.taxCode, a.taxDescription, a.taxPercent")
				.setParameter("providerCode", providerCode).setParameter("startDate", startDate)
				.setParameter("endDate", endDate);
		log.debug("getTaxRecodsBetweenDate : query={0}", query);
		result = query.getResultList();
		log.info("getTaxRecodsBetweenDate : {0} records", result.size());
		return result;
	}

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Object> getJournalRecords(String providerCode, Date startDate, Date endDate) {
		List<Object> result = null;
		log.info("getJournalRecords ( {0}, {1})", startDate, endDate);
		Query query = dwhEntityManager
				.createQuery(
						"select a.type, a.invoiceDate, a.invoiceNumber,a.customerAccountCode, a.accountingCode, sum(a.amountWithoutTax),sum(a.amountTax),sum(a.amountWithTax) from "
								+ getEntityClass().getSimpleName()
								+ " a where a.providerCode=:providerCode and a.invoiceDate>=:startDate and a.invoiceDate<=:endDate"
								+ " group by (a.type, a.invoiceDate, a.invoiceNumber,a.customerAccountCode, a.accountingCode)"
								+ " having sum(a.amountWithoutTax)<>0 or  sum(a.amountTax)<>0 "
								+ " order by a.invoiceNumber,a.accountingCode desc")
				.setParameter("providerCode", providerCode).setParameter("startDate", startDate)
				.setParameter("endDate", endDate);
		log.debug("getJournalRecords : query={0}", query);
		result = query.getResultList();
		log.info("getJournalRecords : {0} records", result.size());
		return result;
	}

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Object> getSIMPACRecords(String providerCode, Date startDate, Date endDate) {
		List<Object> result = null;
		log.info("getSIMPACRecords( {0}, {1})", startDate, endDate);
		Query query = dwhEntityManager
				.createQuery(
						"select a.type,a.accountingCode, sum(amountWithoutTax) as amountWithoutTax , sum(amountTax) as amountTax, sum(amountWithTax) as amountWithTax  from "
								+ getEntityClass().getSimpleName()
								+ " a where a.providerCode=:providerCode and a.invoiceDate>=:startDate and a.invoiceDate<=:endDate "
								+ " group by a.accountingCode,a.type"
								+ " having sum(amountWithoutTax)<>0 or  sum(amountTax)<>0 "
								+ " order by a.accountingCode desc")
				.setParameter("providerCode", providerCode).setParameter("startDate", startDate)
				.setParameter("endDate", endDate);
		log.debug("getSIMPACRecords : query={0}", query);
		result = query.getResultList();
		log.info("getSIMPACRecords : {0} records", result.size());
		return result;
	}
}
