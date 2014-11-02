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

import java.util.Date;
import java.util.List;

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
@Stateless
public class JournalEntryService extends PersistenceService<JournalEntry> {

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Object> getTaxRecodsBetweenDate(String providerCode,
			Date startDate, Date endDate) {
		List<Object> result = null;
		log.info("getTaxRecodsBetweenDate ( {}, {})", startDate, endDate);
		Query query = getEntityManager()
				.createQuery(
						"select a.taxCode, a.taxDescription, a.taxPercent, sum(amountWithoutTax) as amountWithoutTax,  sum(amountTax) as amountTax from "
								+ getEntityClass().getSimpleName()
								+ " a where a.providerCode=:providerCode and a.type='T' and a.invoiceDate>=:startDate and a.invoiceDate <=:endDate group by a.taxCode, a.taxDescription, a.taxPercent")
				.setParameter("providerCode", providerCode)
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate);
		log.debug("getTaxRecodsBetweenDate : query={}", query);
		result = query.getResultList();
		log.info("getTaxRecodsBetweenDate : {} records", result.size());
		return result;
	}

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Object> getJournalRecords(String providerCode, Date startDate,
			Date endDate) {
		List<Object> result = null;
		log.info("getJournalRecords ( {}, {})", startDate, endDate);
		Query query = getEntityManager()
				.createQuery(
						"select a.type, a.invoiceDate, a.invoiceNumber,a.customerAccountCode, a.accountingCode, sum(a.amountWithoutTax),sum(a.amountTax),sum(a.amountWithTax) from "
								+ getEntityClass().getSimpleName()
								+ " a where a.providerCode=:providerCode and a.invoiceDate>=:startDate and a.invoiceDate<=:endDate"
								+ " group by (a.type, a.invoiceDate, a.invoiceNumber,a.customerAccountCode, a.accountingCode)"
								+ " having sum(a.amountWithoutTax)<>0 or  sum(a.amountTax)<>0 "
								+ " order by a.invoiceNumber,a.accountingCode desc")
				.setParameter("providerCode", providerCode)
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate);
		log.debug("getJournalRecords : query={}", query);
		result = query.getResultList();
		log.info("getJournalRecords : {} records", result.size());
		return result;
	}

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Object> getSIMPACRecords(String providerCode, Date startDate,
			Date endDate) {
		List<Object> result = null;
		log.info("getSIMPACRecords( {}, {})", startDate, endDate);
		Query query = getEntityManager()
				.createQuery(
						"select a.type,a.accountingCode, sum(amountWithoutTax) as amountWithoutTax , sum(amountTax) as amountTax, sum(amountWithTax) as amountWithTax  from "
								+ getEntityClass().getSimpleName()
								+ " a where a.providerCode=:providerCode and a.invoiceDate>=:startDate and a.invoiceDate<=:endDate "
								+ " group by a.accountingCode,a.type"
								+ " having sum(amountWithoutTax)<>0 or  sum(amountTax)<>0 "
								+ " order by a.accountingCode desc")
				.setParameter("providerCode", providerCode)
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate);
		log.debug("getSIMPACRecords : query={}", query);
		result = query.getResultList();
		log.info("getSIMPACRecords : {} records", result.size());
		return result;
	}
}
