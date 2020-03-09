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
	public List<Object> getTaxRecodsBetweenDate(Date startDate, Date endDate) {
		List<Object> result = null;
		log.info("getTaxRecodsBetweenDate ( {}, {})", startDate, endDate);
		Query query = getEntityManager()
				.createQuery(
						"select a.taxCode, a.taxDescription, a.taxPercent, sum(amountWithoutTax) as amountWithoutTax,  sum(amountTax) as amountTax from "
								+ getEntityClass().getSimpleName()
								+ " a where a.type='T' and a.invoiceDate>=:startDate and a.invoiceDate <=:endDate group by a.taxCode, a.taxDescription, a.taxPercent")
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate);
		log.debug("getTaxRecodsBetweenDate : query={}", query);
		result = query.getResultList();
		log.info("getTaxRecodsBetweenDate : {} records", result.size());
		return result;
	}

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Object> getJournalRecords(Date startDate,
			Date endDate) {
		List<Object> result = null;
		log.info("getJournalRecords ( {}, {})", startDate, endDate);
		Query query = getEntityManager()
				.createQuery(
						"select a.type, a.invoiceDate, a.invoiceNumber,a.customerAccountCode, a.accountingCode, sum(a.amountWithoutTax),sum(a.amountTax),sum(a.amountWithTax) from "
								+ getEntityClass().getSimpleName()
								+ " a where a.invoiceDate>=:startDate and a.invoiceDate<=:endDate"
								+ " group by (a.type, a.invoiceDate, a.invoiceNumber,a.customerAccountCode, a.accountingCode)"
								+ " having sum(a.amountWithoutTax)<>0 or  sum(a.amountTax)<>0 "
								+ " order by a.invoiceNumber,a.accountingCode desc")
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate);
		log.debug("getJournalRecords : query={}", query);
		result = query.getResultList();
		log.info("getJournalRecords : {} records", result.size());
		return result;
	}

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Object> getSIMPACRecords(Date startDate,
			Date endDate) {
		List<Object> result = null;
		log.info("getSIMPACRecords( {}, {})", startDate, endDate);
		Query query = getEntityManager()
				.createQuery(
						"select a.type,a.accountingCode, sum(amountWithoutTax) as amountWithoutTax , sum(amountTax) as amountTax, sum(amountWithTax) as amountWithTax  from "
								+ getEntityClass().getSimpleName()
								+ " a where a.invoiceDate>=:startDate and a.invoiceDate<=:endDate "
								+ " group by a.accountingCode,a.type"
								+ " having sum(amountWithoutTax)<>0 or  sum(amountTax)<>0 "
								+ " order by a.accountingCode desc")
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate);
		log.debug("getSIMPACRecords : query={}", query);
		result = query.getResultList();
		log.info("getSIMPACRecords : {} records", result.size());
		return result;
	}
}
