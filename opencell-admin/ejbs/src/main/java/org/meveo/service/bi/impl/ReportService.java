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
package org.meveo.service.bi.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;

import org.meveo.model.bi.Report;
import org.meveo.service.base.PersistenceService;

/**
 * Report service implementation.
 * 
 */
@Stateless
public class ReportService extends PersistenceService<Report> {

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Object> getRows(String queryString) {
		Query query = getEntityManager().createNativeQuery(queryString);
		return query.getResultList();
	}

	public List<Object> getBordereauRemiseChequeRecords(Date startDate,
			Date endDate) {

		// Query query = em
		// .createQuery(
		// "select a.accountingCode, a.occDescription, sum(amount) as amount, a.occCode from "
		// + getEntityClass().getSimpleName()
		// +
		// " a where a.category = 0 and  a.transactionDate>:startDate and a.transactionDate <= :endDate  group by a.occCode, a.accountingCode, a.occDescription")
		// .setParameter("startDate", startDate).setParameter("endDate",
		// endDate);
		//
		// return query.getResultList();
		return null;

	}

}
